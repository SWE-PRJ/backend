package com.sweprj.issue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweprj.issue.DTO.IssueRequest;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.exception.InvalidIssuePriorityException;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class IssueServiceTest {
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

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private String getAdminToken() {
        // 유저 계정 생성
        String identifier = "admin";
        String password = "adminPassword";
        User user = new Admin(identifier, passwordEncoder.encode(password));

        userRepository.save(user);

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(identifier, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }

    @Test
    public void createIssue_validToken_createsIssue() throws Exception {

        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle("Test Issue");
        issueRequest.setDescription("Test Description");
        issueRequest.setPriority("major");

        String token = getAdminToken();
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        Long projectId = project.getId();

        mockMvc.perform(post("/api/projects/"+projectId+"/issues")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(issueRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());

        em.flush();
    }

    @Test
    public void createIssue_invalidPriority_throwsException() throws Exception {

        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle("Test Issue");
        issueRequest.setDescription("Test Description");
        issueRequest.setPriority("INVALID_PRIORITY");


        String token = getAdminToken();
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);
        Long projectId = project.getId();

        mockMvc.perform(post("/api/projects/"+projectId+"/issues")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(issueRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidIssuePriorityException));
    }

    // Additional test cases can be added here for other functionalities like updating and retrieving issues
}
