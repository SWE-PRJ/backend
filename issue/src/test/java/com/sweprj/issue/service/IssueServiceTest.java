//package com.sweprj.issue.service;
//
//import com.sweprj.issue.DTO.*;
//import com.sweprj.issue.config.jwt.JwtTokenProvider;
//import com.sweprj.issue.domain.*;
//import com.sweprj.issue.domain.enums.IssuePriority;
//import com.sweprj.issue.domain.enums.IssueState;
//import com.sweprj.issue.exception.InvalidIssuePriorityException;
//import com.sweprj.issue.exception.InvalidIssueStateException;
//import com.sweprj.issue.exception.ResourceNotFoundException;
//import com.sweprj.issue.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class IssueServiceTest {
//
//    @Mock
//    private ProjectRepository projectRepository;
//
//    @Mock
//    private IssueRepository issueRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Mock
//    private ProjectUserRepository projectUserRepository;
//
//    @Mock
//    private Authentication authentication;
//
//    @Mock
//    private SecurityContext securityContext;
//
//    @InjectMocks
//    private IssueService issueService;
//
//    private User user;
//    private Project project;
//    private Issue issue;
//
//    @BeforeEach
//    void setUp() {
//        user = new GeneralUser("testuser", "password");
//        user.setUserId(1L);
//
//        project = new Project();
//        project.setId(1L);
//        project.setName("Test Project");
//
//        issue = new Issue();
//        issue.setId(1L);
//        issue.setTitle("Test Issue");
//        issue.setDescription("Test Description");
//        issue.setReporter(user);
//        issue.setPriority(IssuePriority.blocker);
//        issue.setState(IssueState.NEW);
//        issue.setProject(project);
//        issue.setReportedAt(new Date());
//
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//        when(authentication.getCredentials()).thenReturn("testToken");
//        when(jwtTokenProvider.getUserFromJwt("testToken")).thenReturn(1L);
//    }
//
//    @Test
//    void testCreateIssue() {
//        IssueRequest request = new IssueRequest();
//        request.setTitle("Test Issue");
//        request.setDescription("Test Description");
//        request.setPriority("blocker");
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
//        when(issueRepository.save(any(Issue.class))).thenReturn(issue);
//
//        IssueResponse response = issueService.createIssue(1L, request);
//
//        assertThat(response.getTitle()).isEqualTo(issue.getTitle());
//        assertThat(response.getDescription()).isEqualTo(issue.getDescription());
//    }
//
//    @Test
//    void testCreateIssueWithInvalidPriority() {
//        IssueRequest request = new IssueRequest();
//        request.setTitle("Test Issue");
//        request.setDescription("Test Description");
//        request.setPriority("invalidPriority");
//
//        assertThrows(InvalidIssuePriorityException.class, () -> {
//            issueService.createIssue(1L, request);
//        });
//    }
//
//    @Test
//    void testFindAll() {
//        when(issueRepository.findAll()).thenReturn(List.of(issue));
//
//        IssueListResponse response = issueService.findAll();
//
//        assertThat(response.getIssues()).hasSize(1);
//        assertThat(response.getIssues().get(0).getTitle()).isEqualTo(issue.getTitle());
//    }
//
//    @Test
//    void testFindDTOById() {
//        when(issueRepository.findById(anyLong())).thenReturn(Optional.of(issue));
//
//        IssueResponse response = issueService.findDTOById(1L);
//
//        assertThat(response.getTitle()).isEqualTo(issue.getTitle());
//    }
//
//    @Test
//    void testFindDTOByIdNotFound() {
//        when(issueRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            issueService.findDTOById(1L);
//        });
//    }
//
//    @Test
//    void testFindByProject() {
//        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
//        when(issueRepository.getIssuesByProject(any(Project.class))).thenReturn(List.of(issue));
//
//        IssueListResponse response = issueService.findByProject(1L);
//
//        assertThat(response.getIssues()).hasSize(1);
//        assertThat(response.getIssues().get(0).getTitle()).isEqualTo(issue.getTitle());
//    }
//
//    @Test
//    void testFindIssueAssignedTo() {
//        when(userRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
//        when(issueRepository.getIssuesByAssignee(any(User.class))).thenReturn(List.of(issue));
//
//        IssueListResponse response = issueService.findIssueAssignedTo("testuser");
//
//        assertThat(response.getIssues()).hasSize(1);
//        assertThat(response.getIssues().get(0).getTitle()).isEqualTo(issue.getTitle());
//    }
//
//    @Test
//    void testSetIssueState() {
//        IssueStateRequest request = new IssueStateRequest();
//        request.setState("ASSIGNED");
//
//        when(issueRepository.findById(anyLong())).thenReturn(Optional.of(issue));
//
//        IssueResponse response = issueService.setIssueState(1L, request);
//
//        assertThat(response.getState()).isEqualTo("ASSIGNED");
//    }
//
//    @Test
//    void testSetIssueStateWithInvalidState() {
//        IssueStateRequest request = new IssueStateRequest();
//        request.setState("invalidState");
//
//        when(issueRepository.findById(anyLong())).thenReturn(Optional.of(issue));
//
//        assertThrows(InvalidIssueStateException.class, () -> {
//            issueService.setIssueState(1L, request);
//        });
//    }
//
//    @Test
//    void testSetIssueAssignee() {
//        IssueAssigneeRequest request = new IssueAssigneeRequest();
//        request.setUserIdentifier("testuser");
//
//        when(userRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
//        when(issueRepository.findById(anyLong())).thenReturn(Optional.of(issue));
//        when(projectUserRepository.getProjectUserByProjectAndUser(any(Project.class), any(User.class)))
//                .thenReturn(new ProjectUser());
//
//        IssueResponse response = issueService.setIssueAssignee(1L, request);
//
//        assertThat(response.getAssignee().getUsername()).isEqualTo("testuser");
//    }
//
//    @Test
//    void testGetIssueStatistics() {
//        when(issueRepository.count()).thenReturn(10L);
//        when(issueRepository.countIssuesByState(anyLong())).thenReturn(List.of(new Object[]{"NEW", 5L}));
//        when(issueRepository.countIssuesByPriority(anyLong())).thenReturn(List.of(new Object[]{"blocker", 3L}));
//        when(issueRepository.countIssuesByMonth(anyLong(), any(Date.class), any(Date.class)))
//                .thenReturn(List.of(new Object[]{"2024-05", 7L}));
//        when(issueRepository.countIssuesByDayPerMonth(anyLong(), any(Date.class), any(Date.class)))
//                .thenReturn(List.of(new Object[]{"2024-05", 1, 3L}));
//
//        IssueStatisticsDTO stats = issueService.getIssueStatistics(1L, new Date(), new Date());
//
//        assertThat(stats.getTotalIssues()).isEqualTo(10L);
//        assertThat(stats.getIssuesByStatus().get("NEW")).isEqualTo(5L);
//        assertThat(stats.getIssuesByPriority().get("blocker")).isEqualTo(3L);
//        assertThat(stats.getIssuesByMonth().get("2024-05")).isEqualTo(7L);
//        assertThat(stats.getIssuesByDayPerMonth().get("2024-05").get("1")).isEqualTo(3L);
//    }
//}
