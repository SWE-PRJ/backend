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

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestParam String name) {
        Project project = projectService.createProject(name);
        return ResponseEntity.ok(project);
    }

    @PostMapping("/{projectId}/{identifier}")
    public ResponseEntity<String> addUserToProject(@PathVariable Long projectId, @PathVariable String identifier) {
        try {
            projectService.addUserToProject(projectId, identifier);
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("User added to project successfully");
    }
}
