package com.sweprj.issue.service;

import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.ProjectUser;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.ProjectUserRepository;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public Project createProject(String name) {
        Project project = new Project();
        project.setName(name);
        return projectRepository.save(project);
    }
    public void addUserToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProject(project);
        projectUser.setUser(user);

        projectUserRepository.save(projectUser);
    }

}
