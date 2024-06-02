package com.sweprj.issue.service;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.config.jwt.UserAuthentication;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.repository.CommentRepository;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class IssueServiceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserRepository userRepository;

    private String authToken;
    private String userIdentifier;
    private String userPassword;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private IssueRepository issueRepository;
    private Long projectId;

    @Before
    public void setUp() {
        userIdentifier = "test";
        userPassword = "test";
        Admin user = new Admin(userIdentifier, passwordEncoder.encode(userPassword));
        userRepository.save(user);

        UserLogInRequest request = new UserLogInRequest();
        request.setIdentifier(userIdentifier);
        request.setPassword(userPassword);
        Map<String, Object> response = userService.login(request);
        authToken = (String) response.get("token");

        authenticate();

        ProjectDTO projectDTO = projectService.createProject("test project");
        projectId = projectDTO.getId();
    }

    @Test
    public void createIssue() {
        // given
        String title = "test issue";
        String content = "test content";
        String priority = "major";
        // when
        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle(title);
        issueRequest.setDescription(content);
        issueRequest.setPriority(priority);
        Long issueId = issueService.createIssue(projectId, issueRequest).getId();

        // then
        assertThat(issueRepository.findById(issueId).isPresent()).isTrue();
    }

    @Test
    public void getIssue() {
        // given
        String title = "test issue";
        String content = "test content";
        String priority = "major";
        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle(title);
        issueRequest.setDescription(content);
        issueRequest.setPriority(priority);
        Long issueId = issueService.createIssue(projectId, issueRequest).getId();

        // when
        IssueResponse issueResponse = issueService.findDTOById(issueId);

        // then
        assertThat(issueResponse.getTitle()).isEqualTo(title);
        assertThat(issueResponse.getDescription()).isEqualTo(content);
        assertThat(issueResponse.getPriority().toString()).isEqualTo(priority);
        assertThat(issueResponse.getState().toString()).isEqualTo("NEW");
    }

    @Test
    public void testFindByProject() {
        // given
        String title = "test issue";
        String content = "test content";
        String priority = "major";
        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle(title);
        issueRequest.setDescription(content);
        issueRequest.setPriority(priority);
        Long issueId = issueService.createIssue(projectId, issueRequest).getId();

        // when
        IssueListResponse issueResponses = issueService.findByProject(projectId);

        // then
        assertThat(issueResponses.getIssues().size()).isEqualTo(1);
        assertThat(issueResponses.getIssues().get(0).getTitle()).isEqualTo(title);
    }
    @Test
    public void testSetIssueAssignee_AND_findIssuesAssignedTo(){
        // given
        String title = "test issue";
        String content = "test content";
        String priority = "major";
        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle(title);
        issueRequest.setDescription(content);
        issueRequest.setPriority(priority);
        Long issueId = issueService.createIssue(projectId, issueRequest).getId();

        IssueAssigneeRequest issueAssigneeRequest = new IssueAssigneeRequest();
        issueAssigneeRequest.setUserIdentifier(userIdentifier);
        issueService.setIssueAssignee(issueId, issueAssigneeRequest);

        // when
        IssueListResponse issueResponses = issueService.findIssuesAssignedTo(projectId, userIdentifier);

        // then
        assertThat(issueResponses.getIssues().size()).isEqualTo(1);
        assertThat(issueResponses.getIssues().get(0).getTitle()).isEqualTo(title);
    }

    @Test
    public void testGetStatistics(){
        // given
        String title1 = "test issue";
        String content1 = "test content";
        String priority1 = "major";
        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle(title1);
        issueRequest.setDescription(content1);
        issueRequest.setPriority(priority1);
        Long issueId = issueService.createIssue(projectId, issueRequest).getId();

        String title2 = "test issue2";
        String content2 = "test content2";
        String priority2 = "minor";
        IssueRequest issueRequest2 = new IssueRequest();
        issueRequest2.setTitle(title2);
        issueRequest2.setDescription(content2);
        issueRequest2.setPriority(priority2);
        Long issueId2 = issueService.createIssue(projectId, issueRequest2).getId();

        // when
        IssueStatisticsDTO issueStatisticsDTO = issueService.getIssueStatistics(projectId, new Date(123, 1, 1, 0, 0, 0), new Date());

        // then
        assertThat(issueStatisticsDTO.getIssuesByMonth().size()).isEqualTo(1);
        assertThat(issueStatisticsDTO.getIssuesByPriority().size()).isEqualTo(2);
        assertThat(issueStatisticsDTO.getIssuesByStatus().size()).isEqualTo(1);
    }

    @Test
    public void testDeleteIssue(){
        // given
        String title = "test issue";
        String content = "test content";
        String priority = "major";
        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle(title);
        issueRequest.setDescription(content);
        issueRequest.setPriority(priority);
        Long issueId = issueService.createIssue(projectId, issueRequest).getId();

        // when
        issueService.deleteIssue(issueId);

        // then
        assertThat(issueRepository.findById(issueId).isPresent()).isFalse();
    }

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
