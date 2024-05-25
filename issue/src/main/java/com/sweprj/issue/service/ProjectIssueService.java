package com.sweprj.issue.service;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.repository.ProjectIssueRepository;
import com.sweprj.issue.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectIssueService {

    private final ProjectRepository projectRepository;
    private final ProjectIssueRepository projectIssueRepository;

    public ProjectIssueService(ProjectRepository projectRepository, ProjectIssueRepository projectIssueRepository) {
        this.projectRepository = projectRepository;
        this.projectIssueRepository = projectIssueRepository;
    }

    public Issue createIssue(Issue issue) {
        return projectIssueRepository.save(issue);
    }

    public Issue createIssue(Long projectId, String title, String description) {
        Issue issue = new Issue();
        issue.setProject(projectRepository.getById(projectId));
        issue.setTitle(title);
        issue.setDescription(description);
        return projectIssueRepository.save(issue);
    }

    public List<Issue> findAll() {
        return projectIssueRepository.findAll();
    }

    public  List<Issue> findByProjectAndIssue(Long projectId, String state) {
        return projectIssueRepository.getIssuesByProjectAndState(projectRepository.getById(projectId), state);
    }

    public Optional<Issue> findById(Long id) {
        return projectIssueRepository.findById(id);
    }

    public List<Issue> findByProject(Long projectId) {
        return projectIssueRepository.getIssuesByProject(projectRepository.getById(projectId));
    }
}
