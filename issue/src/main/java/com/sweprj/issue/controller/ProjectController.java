package com.sweprj.issue.controller;

import com.sweprj.issue.domain.Project;
import com.sweprj.issue.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/create")
    public ResponseEntity<Project> createProject(@RequestParam String name) {
        Project project = projectService.createProject(name);
        return ResponseEntity.ok(project);
    }

    @PostMapping("/{projectId}/addUser/{userId}")
    public ResponseEntity<String> addUserToProject(@PathVariable Long projectId, @PathVariable Long userId) {
        try {
            projectService.addUserToProject(projectId, userId);
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("User added to project successfully");
    }
}
