package com.sweprj.issue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.domain.Comment;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.repository.CommentRepository;
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

import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CommentControllerTest {

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

    @Autowired
    private CommentRepository commentRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private String getAdminToken() {
        // 유저 계정이 이미 존재하는지 확인
        String identifier = "adminTest";
        String rawPassword = "adminPassword";
        User user = userRepository.findByIdentifier(identifier).orElse(null);

        if (user == null) {
            // 존재하지 않는 경우에만 새로운 유저 생성
            user = new Admin(identifier, passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        }

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(identifier, rawPassword);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }

    @Test
    public void createComment_validToken_createsComment() throws Exception {
        String token = getAdminToken();
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);

        Issue issue = new Issue();
        issue.setTitle("Test Issue");
        issue.setProject(project);
        issueRepository.save(issue);

        Long issueId = issue.getId();
        String content = "Test Comment";

        mockMvc.perform(post("/api/issues/" + issueId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .param("content", content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.issueId").value(issueId));
    }

    @Test
    public void getComments_validToken_returnsComments() throws Exception {
        String token = getAdminToken();
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);

        Issue issue = new Issue();
        issue.setTitle("Test Issue");
        issue.setProject(project);
        issueRepository.save(issue);

        Comment comment = new Comment();
        comment.setIssue(issue);
        comment.setCommenter(userRepository.findByIdentifier("adminTest").orElseThrow());
        comment.setContent("Test Comment");
        comment.setCommentedAt(new java.util.Date());
        commentRepository.save(comment);

        Long issueId = issue.getId();

        mockMvc.perform(get("/api/issues/" + issueId + "/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].issueId").value(issueId));
    }

    @Test
    public void updateComment_validToken_updatesComment() throws Exception {
        String token = getAdminToken();
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);

        Issue issue = new Issue();
        issue.setTitle("Test Issue");
        issue.setProject(project);
        issueRepository.save(issue);

        Comment comment = new Comment();
        comment.setIssue(issue);
        comment.setCommenter(userRepository.findByIdentifier("adminTest").orElseThrow());
        comment.setContent("Test Comment");
        comment.setCommentedAt(new java.util.Date());
        commentRepository.save(comment);

        Long issueId = issue.getId();
        Long commentId = comment.getId();
        String updatedContent = "Updated Comment";

        mockMvc.perform(patch("/api/issues/" + issueId + "/comments/" + commentId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(Map.of("content", updatedContent))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(updatedContent))
                .andExpect(jsonPath("$.issueId").value(issueId));
    }

    @Test
    public void deleteComment_validToken_deletesComment() throws Exception {
        String token = getAdminToken();
        Project project = new Project();
        project.setName("Test Project");
        projectRepository.save(project);

        Issue issue = new Issue();
        issue.setTitle("Test Issue");
        issue.setProject(project);
        issueRepository.save(issue);

        Comment comment = new Comment();
        comment.setIssue(issue);
        comment.setCommenter(userRepository.findByIdentifier("adminTest").orElseThrow());
        comment.setContent("Test Comment");
        comment.setCommentedAt(new java.util.Date());
        commentRepository.save(comment);

        Long issueId = issue.getId();
        Long commentId = comment.getId();

        mockMvc.perform(delete("/api/issues/" + issueId + "/comments/" + commentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));
    }
}

