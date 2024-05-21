package com.sweprj.issue.repository;

import com.sweprj.issue.domain.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Integer> {

}
