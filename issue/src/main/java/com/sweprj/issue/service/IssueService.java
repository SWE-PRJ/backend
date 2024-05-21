package com.sweprj.issue.service;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

public class IssueService {

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;

    public IssueService(ProjectRepository projectRepository, IssueRepository issueRepository) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
    }

    public Issue createIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    public Issue createIssue(Long projectId, String title, String description) {
        Issue issue = new Issue();
        issue.setProject(projectRepository.getById(projectId));
        issue.setTitle(title);
        issue.setDescription(description);
        return issueRepository.save(issue);
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    public  List<Issue> findByState(Long projectId, String state) {
        return issueRepository.findByState(projectId, state);
    }

    public Optional<Issue> findById(Long id) {
        return issueRepository.findById(id);
    }

    public List<Issue> findAllIn(Long projectId) {
        return issueRepository.findAllIn(projectId);
    }
}
