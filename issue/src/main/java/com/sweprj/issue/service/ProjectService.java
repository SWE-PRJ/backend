package com.sweprj.issue.service;

import com.sweprj.issue.DTO.ProjectDTO;
import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.ProjectUser;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.ProjectUserRepository;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectUserRepository projectUserRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectUserRepository = projectUserRepository;
    }

    public ProjectDTO createProject(String name) {
        Project project = new Project();
        project.setName(name);
        Project savedProject = projectRepository.save(project);
        return convertToDTO(savedProject);
    }
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProjectDTO updateProject(Long projectId, String name) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));
        project.setName(name);
        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));
        projectRepository.delete(project);
    }
    
    public void addUserToProject(Long projectId, String identifier) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));

        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new RuntimeException("User not found with identifier " + identifier));

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProject(project);
        projectUser.setUser(user);

        projectUserRepository.save(projectUser);
    }

    private ProjectDTO convertToDTO(Project project) {
        return new ProjectDTO(project.getId(), project.getName());
    }

}
