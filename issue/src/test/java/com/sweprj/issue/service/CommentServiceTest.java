package com.sweprj.issue.service;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.config.jwt.UserAuthentication;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.domain.enums.IssuePriority;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CommentServiceTest {

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

    private Long issueId;

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
        Long projectId = projectDTO.getId();

        IssueRequest issueRequest = new IssueRequest();
        issueRequest.setTitle("test title");
        issueRequest.setDescription("test content");
        issueRequest.setPriority(IssuePriority.major.toString());
        IssueResponse issueResponse = issueService.createIssue(projectId, issueRequest);
        issueId = issueResponse.getId();
    }

    @Test
    public void testCreateComment() {
        // Given
        String content = "test content";

        // When
        CommentDTO comment = commentService.createComment(issueId, content);

        // Then
        assertThat(comment.getContent()).isEqualTo(content);
    }

    @Test
    public void testCreateCommentFail() {
        // Given
        String content = "test content";

        // When
        CommentDTO comment = commentService.createComment(issueId, content);

        // Then
        assertThat(comment.getContent()).isNotEqualTo("test");
    }

    @Test
    public void testGetComment() {
        // Given
        String content = "test content";

        CommentDTO comment = commentService.createComment(issueId, content);
        // When
        CommentDTO foundComment = commentService.getComment(comment.getId());

        // Then
        assertThat(foundComment.getContent()).isEqualTo(content);
    }

    @Test
    public void testGetAllComments() {
        // Given
        String content = "test content";

        // When
        CommentDTO comment = commentService.createComment(issueId, content);
        List<CommentDTO> comments = commentService.getAllComments(issueId);

        // Then
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void testUpdateComment() {
        // Given
        String content = "test content";
        CommentDTO comment = commentService.createComment(issueId, content);
        String updatedContent = "updated content";

        // When
        CommentDTO updatedComment = commentService.updateComment(comment.getId(), updatedContent);

        // Then
        assertThat(updatedComment.getContent()).isEqualTo(updatedContent);
    }

    @Test
    public void testDeleteComment() {
        // Given
        String content = "test content";
        CommentDTO comment = commentService.createComment(issueId, content);

        // When
        commentService.deleteComment(comment.getId());

        // Then
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
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
