package com.sweprj.issue.service;

import com.sweprj.issue.DTO.UserLogInRequest;
import com.sweprj.issue.DTO.UserResponse;
import com.sweprj.issue.DTO.UserSignInRequest;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.config.jwt.UserAuthentication;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private String authToken;
    private User testUser;

    @Before
    public void setUp() {
        // Setup an admin user and log in to get an auth token
        String identifier = "admin";
        String password = "adminPassword";
        testUser = new Admin(identifier, passwordEncoder.encode(password));
        userRepository.save(testUser);

        UserLogInRequest request = new UserLogInRequest();
        request.setIdentifier(identifier);
        request.setPassword(password);
        Map<String, Object> response = userService.login(request);
        authToken = (String) response.get("token");

        authenticate();
    }

    @Test
    public void testRegister() {
        // Given
        UserSignInRequest signInRequest = new UserSignInRequest();
        signInRequest.setIdentifier("newTester");
        signInRequest.setPassword("testPassword");

        // When
        Long userId = userService.register(signInRequest, "tester");

        // Then
        User createdUser = userRepository.findById(userId).orElse(null);
        assertNotNull(createdUser);
        assertEquals("newTester", createdUser.getIdentifier());
        assertEquals("ROLE_TESTER", createdUser.getRole());
    }

    @Test
    public void testLogin() {
        // Given
        String identifier = "loginUser";
        String password = "loginPassword";
        User newUser = new Admin(identifier, passwordEncoder.encode(password));
        userRepository.save(newUser);

        UserLogInRequest loginRequest = new UserLogInRequest();
        loginRequest.setIdentifier(identifier);
        loginRequest.setPassword(password);

        // When
        Map<String, Object> response = userService.login(loginRequest);

        // Then
        assertNotNull(response.get("token"));
        assertEquals(newUser.getUserId(), response.get("id"));
        assertEquals(newUser.getRole(), response.get("role"));
    }

    @Test
    public void testFindById() {
        // Given
        Long userId = testUser.getUserId();

        // When
        User foundUser = userService.findById(userId);

        // Then
        assertNotNull(foundUser);
        assertEquals(testUser.getIdentifier(), foundUser.getIdentifier());
    }

    @Test
    public void testLoadUserByUsername() {
        // Given
        String identifier = testUser.getIdentifier();

        // When
        UserDetails userDetails = userService.loadUserByUsername(identifier);

        // Then
        assertNotNull(userDetails);
        assertEquals(identifier, userDetails.getUsername());
    }

//    @Test
//    public void testGetAllUsers() {
//        // Given
//        UserSignInRequest signInRequest = new UserSignInRequest();
//        signInRequest.setIdentifier("newUser");
//        signInRequest.setPassword("newPassword");
//        userService.signup(signInRequest, "tester");
//
//        // When
//        List<UserResponse> users = userService.getAllUsers();
//
//        // Then
//        assertTrue(users.size() >= 1); // Ensure at least the admin and new user are present
//    }
//
//    @Test
//    public void testGetUserResponse() {
//        // Given
//        Long userId = testUser.getUserId();
//
//        // When
//        UserResponse userResponse = userService.getUserResponse(userId);
//
//        // Then
//        assertNotNull(userResponse);
//        assertEquals(testUser.getIdentifier(), userResponse.getIdentifier());
//    }
//
//    @Test
//    public void testGetMe() {
//        // When
//        UserResponse userResponse = userService.getMe();
//
//        // Then
//        assertNotNull(userResponse);
//        assertEquals(testUser.getIdentifier(), userResponse.getIdentifier());
//    }

    private void authenticate() {
        Long memberId = jwtTokenProvider.getUserFromJwt(authToken);
        String role = jwtTokenProvider.getRoleFromJwt(authToken);
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        // 사용자 인증 객체 생성
        UserAuthentication authentication = new UserAuthentication(memberId.toString(), authToken, authorities);

        // SecurityContextHolder에 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
