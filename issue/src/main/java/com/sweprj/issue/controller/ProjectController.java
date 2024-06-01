package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.ProjectDTO;
import com.sweprj.issue.DTO.ProjectUsersResponse;
import com.sweprj.issue.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestParam String name) {
        ProjectDTO project = projectService.createProject(name);
        return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long projectId, @RequestParam String name) {
        ProjectDTO project = projectService.updateProject(projectId, name);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted successfully");
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

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectUsersResponse> getUsersInProject(@PathVariable Long projectId) {
        ProjectUsersResponse projectUsersResponse;
        projectUsersResponse= projectService.getUsersInProject(projectId);
        return ResponseEntity.ok(projectUsersResponse);
    }
}
