package com.sweprj.issue.repository;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectIssueRepository extends JpaRepository<Issue, Long> {

    Issue getIssueById(Long id);

    List<Issue> getIssuesByProjectAndState(Project project, String state);

    List<Issue> getIssuesByProject(Project project);
}
