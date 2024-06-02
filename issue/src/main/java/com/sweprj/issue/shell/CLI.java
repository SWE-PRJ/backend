package com.sweprj.issue.shell;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.config.jwt.UserAuthentication;
import com.sweprj.issue.service.CommentService;
import com.sweprj.issue.service.IssueService;
import com.sweprj.issue.service.ProjectService;
import com.sweprj.issue.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ShellComponent
@RequiredArgsConstructor
@Transactional
public class CLI {

    private final JwtTokenProvider jwtTokenProvider;
    private final ProjectService projectService;
    private final IssueService issueService;
    private final UserService userService;
    private final CommentService commentService;

    private Long currentUserId;
    private String currentUserRole;
    private String authToken;

    @ShellMethod("Sign up")
    public String signup(String identifier, String password, String role) {
        authenticate();
        if (!currentUserRole.equals("ROLE_ADMIN")) {
            return "Only admin can sign up new users";
        }
        UserSignInRequest request = new UserSignInRequest();
        request.setIdentifier(identifier);
        request.setPassword(password);
        Long userId = userService.signup(request, role);
        return "Signed up successfully. User ID: " + userId;
    }

    @ShellMethod("Log in")
    public String login(String username, String password) {
        UserLogInRequest request = new UserLogInRequest();
        request.setIdentifier(username);
        request.setPassword(password);
        Map<String, Object> response = userService.login(request);
        if (response.containsKey("error")) {
            return (String) response.get("error");
        } else {
            currentUserId = (Long) response.get("id");
            currentUserRole = (String) response.get("role");
            authToken = (String) response.get("token");
            return "Logged in successfully. \n Welcome, " + username;
        }
    }

    @ShellMethod("Create a new project")
    public String createProject(String name) {
        authenticate();
        ProjectDTO project = projectService.createProject(name);
        return "Project created: " + project.getName();
    }

    @ShellMethod("List all projects")
    public String listProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            return "No projects found";
        }
        authenticate();
        StringBuilder out = new StringBuilder("Projects: \n");
        for (ProjectDTO project : projects) {
            out.append(project.getId()).append(": ").append(project.getName()).append("\n");
        }
        return out.toString();
    }

    @ShellMethod("Add user to project")
    public String addUserToProject(Long projectId, String identifier) {
        authenticate();
        projectService.addUserToProject(projectId, identifier);
        return "User added to project";
    }

    @ShellMethod("List issues in a project")
    public String listIssues(@ShellOption Long projectId) {
        authenticate();
        StringBuilder out = new StringBuilder("Issues: \n");
        IssueListResponse issues = issueService.findByProject(projectId);
        if (issues.getIssues().isEmpty()) {
            return "No issues found";
        }
        for (IssueResponse issue : issues.getIssues()) {
            out.append(issue.getId()).append(": ").append(issue.getTitle()).append("\n");
        }

        return out.toString();
    }

    @ShellMethod("Show details of an issue")
    public String showIssueDetails(@ShellOption Long issueId) {
        authenticate();
        IssueResponse issue = issueService.findDTOById(issueId);
        if (issue == null) {
            return "Issue not found";
        }
        return "Issue: " + issue.getTitle() + "\nDescription: " + issue.getDescription() + "\nStatus: " + issue.getState() + "\nPriority: " + issue.getPriority();
    }

    @ShellMethod("Create a new issue")
    public String createIssue(@ShellOption Long projectId, @ShellOption String title, @ShellOption String description) {
        authenticate();
        IssueRequest issueRequestDTO = new IssueRequest();
        issueRequestDTO.setTitle(title);
        issueRequestDTO.setDescription(description);
        issueRequestDTO.setPriority("major");

        IssueResponse issue = issueService.createIssue(projectId, issueRequestDTO);
        return "Issue created: " + issue.getTitle();
    }
    @ShellMethod("Create a comment")
    public String createComment(Long issueId, String content) {
        authenticate();
        StringBuilder out = new StringBuilder("Comment Created: \n");
        CommentDTO comment = commentService.createComment(issueId, content);
        out.append(comment.getId()).append(": ").append(comment.getContent()).append("\n");
        return out.toString();
    }

    @ShellMethod("Get a comment")
    public String getComment(Long commentId) {
        authenticate();
        StringBuilder out = new StringBuilder("Comment: \n");
        CommentDTO comment = commentService.getComment(commentId);
        if (comment == null) {
            return "Comment not found";
        }
        out.append(comment.getId()).append(": ").append(comment.getContent()).append("\n");
        return out.toString();
    }

    @ShellMethod("Get all comments for an issue")
    public String getAllComments(Long issueId) {
        authenticate();
        StringBuilder out = new StringBuilder("Comments: \n");
        List<CommentDTO> comments = commentService.getAllComments(issueId);
        if (comments.isEmpty()) {
            return "No comments found";
        }
        for (CommentDTO comment : comments) {
            out.append(comment.getId()).append(": ").append(comment.getContent()).append("\n");
        }
        return out.toString();
    }

    @ShellMethod("Update a comment")
    public String updateComment(Long commentId, String content) {
        authenticate();
        CommentDTO commentDTO = commentService.updateComment(commentId, content);
        return "Comment updated: " + commentDTO.getContent();
    }

    @ShellMethod("Delete a comment")
    public String deleteComment(Long commentId) {
        authenticate();
        commentService.deleteComment(commentId);
        return "Comment deleted";
    }

    @ShellMethod("Get Statistics")
    public String getStatistics(@ShellOption Long projectId, @ShellOption String StartDate, @ShellOption String EndDate) {
        authenticate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start = null;
        Date end = null;
        try{
            start = formatter.parse(StartDate);
            end = formatter.parse(EndDate);
        } catch (Exception e) {
            return "Invalid date format";
        }
        IssueStatisticsDTO raw = issueService.getIssueStatistics(projectId, start, end);
        return raw.toTable();

    }

    private void authenticate(){
        if (authToken == null) {
            throw new RuntimeException("You must be logged in to perform this action");
        }
        Long memberId = jwtTokenProvider.getUserFromJwt(authToken);
        String role = jwtTokenProvider.getRoleFromJwt(authToken);
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        // 사용자 인증 객체 생성
        UserAuthentication authentication = new UserAuthentication(memberId.toString(), authToken, authorities);

        // SecurityContextHolder에 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
