package com.sweprj.issue.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
@RequiredArgsConstructor
public class CLI {

//    private final ProjectService projectService;
//    private final IssueService issueService;
//    private final UserService userService;
//
//    private Long currentUserId;
//    private String currentUserRole;
//
//    @ShellMethod("Sign up")
//    public String signup(String username, String password, String role) {
//        UserSignInRequest request = new UserSignInRequest();
//        request.setName(username);
//        request.setPassword(password);
//        Long userId = userService.signup(request, role);
//        return "Signed up successfully. User ID: " + userId;
//    }
//
//    @ShellMethod("Register a new admin")
//    public String registerAdmin(String username, String password, String role, String adminIdentifier) {
//        UserSignInRequest request = new UserSignInRequest();
//        request.setName(username);
//        request.setPassword(password);
//        Long userId = userService.register(request, role, adminIdentifier);
//        return "Admin registered successfully. User ID: " + userId;
//    }
//
//    @ShellMethod("Log in")
//    public String login(String username, String password) {
//        UserLogInRequest request = new UserLogInRequest();
//        request.setIdentifier(username);
//        request.setPassword(password);
//        Map<String, Object> response = userService.login(request);
//        if (response.containsKey("error")) {
//            return (String) response.get("error");
//        } else {
//            currentUserId = (Long) response.get("id");
//            currentUserRole = (String) response.get("role");
//            return "Logged in successfully. User ID: " + currentUserId;
//        }
//    }
//
//    @ShellMethod("Create a new project")
//    public String createProject(String name) {
//        if (currentUserId == null) {
//            return "You must be logged in to create a project";
//        }
//        ProjectDTO project = projectService.createProject(name);
//        return "Project created: " + project.getName();
//    }
//
//    @ShellMethod("List all projects")
//    public List<ProjectDTO> listProjects() {
//        return projectService.getAllProjects();
//    }
//
//    @ShellMethod("Add user to project")
//    public String addUserToProject(Long projectId, Long userId) {
//        if (currentUserId == null) {
//            return "You must be logged in to add users to a project";
//        }
//        projectService.addUserToProject(projectId, userId);
//        return "User added to project";
//    }
//
//    @ShellMethod("List issues in a project")
//    public List<IssueResponseDTO> listIssues(@ShellOption Long projectId) {
//        return issueService.findByProject(projectId);
//    }
//
//    @ShellMethod("Show details of an issue")
//    public String showIssueDetails(@ShellOption Long issueId) {
//        IssueResponseDTO issue = issueService.findDTOById(issueId);
//        if (issue == null) {
//            return "Issue not found";
//        }
//        return "Issue: " + issue.getTitle() + "\nDescription: " + issue.getDescription() + "\nStatus: " + issue.getState() + "\nPriority: " + issue.getPriority();
//    }
//
//    @ShellMethod("Create a new issue")
//    public String createIssue(@ShellOption Long projectId, @ShellOption String title, @ShellOption String description) {
//        if (currentUserId == null) {
//            return "You must be logged in to create an issue";
//        }
//        IssueRequestDTO issueRequestDTO = new IssueRequestDTO();
//        issueRequestDTO.setTitle(title);
//        issueRequestDTO.setDescription(description);
//        User reporter = userService.findById(currentUserId);
//        IssueResponseDTO issue = issueService.createIssue(projectId, reporter, issueRequestDTO);
//        return "Issue created: " + issue.getTitle();
//    }
//
//    @ShellMethod("Edit an issue")
//    public String editIssue(@ShellOption Long issueId, @ShellOption String title, @ShellOption String description) {
//        if (currentUserId == null) {
//            return "You must be logged in to edit an issue";
//        }
//        Issue issue = issueService.editIssue(issueId, title, description);
//        return "Issue updated: " + issue.getTitle();
//    }
}
