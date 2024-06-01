package com.sweprj.issue.service;

import com.sweprj.issue.DTO.IssueRequest;
import com.sweprj.issue.DTO.IssueResponse;
import com.sweprj.issue.config.WithMockCustomUser;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.domain.enums.IssuePriority;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private IssueService issueService;

    @BeforeEach
    void setUp() {
        // Setting up common mocks
    }

    @Test
    @WithMockCustomUser(userId = 1L, role = "ROLE_USER")
    void testCreateIssue() {
        // Set up mocks
        Long userId = 1L;
        Long projectId = 1L;

        User mockUser = new Admin();
        mockUser.setUserId(userId);

        Issue issue = new Issue();
        issue.setId(1L);
        issue.setTitle("Test Issue");
        issue.setPriority(IssuePriority.major);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(Mockito.mock(com.sweprj.issue.domain.Project.class)));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        // Prepare IssueRequest
        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle("Test Issue");
        issueRequest.setDescription("Test Description");
        issueRequest.setPriority("major");

        // Test createIssue method
        IssueResponse issueResponse = issueService.createIssue(projectId, issueRequest);

        // Assertions
        assertEquals("Test Issue", issueResponse.getTitle());
        assertEquals(1L, issueResponse.getId());
        assertEquals("Test Description", issueResponse.getDescription());
    }
}
