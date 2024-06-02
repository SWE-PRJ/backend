package com.sweprj.issue.service;

import com.sweprj.issue.DTO.ProjectDTO;
import com.sweprj.issue.DTO.ProjectUsersResponse;
import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.ProjectUser;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Tester;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.ProjectUserRepository;
import com.sweprj.issue.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProjectUserRepository projectUserRepository;

    @Test
    public void testCreateProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        Mockito.when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDTO projectDTO = projectService.createProject("Test Project");

        assertEquals("Test Project", projectDTO.getName());
        assertEquals(1L, projectDTO.getId());
    }

    @Test
    public void testAddUserToProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        User user = new Tester();
        user.setUserId(1L);
        user.setIdentifier("testUser");

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        Mockito.when(userRepository.findByIdentifier(anyString())).thenReturn(Optional.of(user));
        Mockito.when(projectUserRepository.save(any(ProjectUser.class))).thenReturn(new ProjectUser());

        projectService.addUserToProject(1L, "testUser");

        Mockito.verify(projectUserRepository, Mockito.times(1)).save(any(ProjectUser.class));
    }

    @Test
    public void testGetAllProjects() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");

        List<Project> projects = Arrays.asList(project1, project2);

        Mockito.when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectDTO> projectDTOs = projectService.getAllProjects();

        assertEquals(2, projectDTOs.size());
        assertEquals("Project 1", projectDTOs.get(0).getName());
        assertEquals("Project 2", projectDTOs.get(1).getName());
    }

    @Test
    public void testUpdateProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Old Project");

        Mockito.when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        Mockito.when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDTO updatedProject = projectService.updateProject(1L, "Updated Project");

        assertEquals("Updated Project", updatedProject.getName());
    }

    @Test
    public void testDeleteProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        Mockito.when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        projectService.deleteProject(1L);

        Mockito.verify(projectRepository, Mockito.times(1)).delete(project);
    }

    @Test
    public void testGetUsersInProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        User user1 = new Tester();
        user1.setUserId(1L);
        user1.setIdentifier("user1");

        User user2 = new Tester();
        user2.setUserId(2L);
        user2.setIdentifier("user2");

        ProjectUser projectUser1 = new ProjectUser();
        projectUser1.setProject(project);
        projectUser1.setUser(user1);

        ProjectUser projectUser2 = new ProjectUser();
        projectUser2.setProject(project);
        projectUser2.setUser(user2);

        List<ProjectUser> projectUsers = Arrays.asList(projectUser1, projectUser2);

        Mockito.when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        Mockito.when(projectUserRepository.getProjectUsersByProject(any(Project.class))).thenReturn(projectUsers);

        ProjectUsersResponse response = projectService.getUsersInProject(1L);

        assertEquals(2, response.getUserList().size());
        assertEquals("user1", response.getUserList().get(0).getIdentifier());
        assertEquals("user2", response.getUserList().get(1).getIdentifier());
    }
}
