package com.sweprj.issue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweprj.issue.DTO.UserLogInRequest;
import com.sweprj.issue.DTO.UserSignInRequest;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.controller.UserController;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.exception.GlobalExceptionHandler;
import com.sweprj.issue.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

//    @Before
//    public void setup() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
//                .apply(springSecurity())
//                .setControllerAdvice(new GlobalExceptionHandler())
//                .build();
//    }
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private String getAdminToken() {
        // 관리자 계정 생성
        String identifier = "admin";
        String password = "adminPassword";
        User admin = new Admin(identifier, passwordEncoder.encode(password));
        userRepository.save(admin);

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(identifier, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }

    @Test
    public void adminSignup() throws Exception {
        UserSignInRequest request = new UserSignInRequest("password", "admin");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("role", "admin"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());

        em.flush();
    }

//    @Test(expected = DataIntegrityViolationException.class)
@Test
public void adminSignupDuplicateUsername() throws Exception {
    UserSignInRequest request = new UserSignInRequest("password", "admin");

    // 첫 번째 회원가입 요청
    mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(request))
                    .param("role", "admin"))
            .andExpect(status().isCreated());

    em.flush();
    em.clear(); // 영속성 컨텍스트를 명확히 초기화

    // 두 번째 회원가입 요청 시 예외를 기대하지만, 실제로는 HTTP 상태 코드를 검증
    mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(request))
                    .param("role", "admin"))
            .andExpect(status().isConflict()) // 여기서 직접 상태 코드를 검증
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataIntegrityViolationException)) // 예외 타입도 검증 가능
            .andExpect(jsonPath("$.message").value("이미 존재하는 닉네임입니다.")); // 에러 메시지도 검증
}


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void adminRegisterUser() throws Exception {
        String token = getAdminToken();

        UserSignInRequest request = new UserSignInRequest("password", "newUser");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("role", "dev"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());

        em.flush();
    }

    @Test
    public void adminRegisterUserDuplicateUsername() throws Exception {
        String token = getAdminToken();

        UserSignInRequest request = new UserSignInRequest("password", "duplicateDev");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("role", "dev"))
                .andExpect(status().isCreated());

        em.flush();
        em.clear(); // 영속성 컨텍스트를 명확히 초기화

        // 두 번째 회원가입 요청 시 예외를 기대하지만, 실제로는 HTTP 상태 코드를 검증
        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("role", "dev"))
                .andExpect(status().isConflict()) // 여기서 직접 상태 코드를 검증
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataIntegrityViolationException)) // 예외 타입도 검증 가능
                .andExpect(jsonPath("$.message").value("이미 존재하는 닉네임입니다.")); // 에러 메시지도 검증
    }


    @Test
    public void loginSuccess() throws Exception {
        UserSignInRequest request = new UserSignInRequest("password", "loginUser");
        userService.signup(request, "admin");

        UserLogInRequest loginRequest = new UserLogInRequest("loginUser", "password");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));

        em.flush();
    }

    @Test
    public void loginFailure() throws Exception {
        UserLogInRequest loginRequest = new UserLogInRequest("password", "nonExistentUser");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
