package com.sweprj.issue.repository;

import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Integer> {
    List<ProjectUser> getProjectUsersByProject(Project project);
}
