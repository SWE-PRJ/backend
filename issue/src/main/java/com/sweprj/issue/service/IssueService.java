package com.sweprj.issue.service;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.enums.IssueState;
import com.sweprj.issue.exception.ResourceNotFoundException;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class IssueService {

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public IssueService(ProjectRepository projectRepository, IssueRepository issueRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    //이슈 생성 (TESTER)
    public IssueResponse createIssue(Long projectId, User reporter, IssueRequest issueRequest) {
        Issue issue = new Issue();

        issue.setTitle(issueRequest.getTitle());
        issue.setDescription(issueRequest.getDescription());
        issue.setReporter(reporter);
        issue.setPriority(issueRequest.getPriority());
        issue.setProject(projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found")));
        issue.setReportedAt(new Date());

        issueRepository.save(issue);

        return new IssueResponse(issue);
    }

    //모든 이슈 검색 (PL)
    public IssueListResponse findAll() {
        IssueListResponse issueListResponse = new IssueListResponse();
        List<Issue> issues = issueRepository.findAll();

        issueListResponse.addAllIssues(issues);

        return issueListResponse;
    }

    //이슈 상세정보 확인 (PL, DEV, TESTER)
    public IssueResponse findDTOById(Long id) {
        return new IssueResponse(issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found")));
    }

    public Issue findById(Long id) {
        return issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
    }


    //프로젝트 전체 이슈 검색 (PL)
    public IssueListResponse findByProject(Long projectId) {
        IssueListResponse issueListResponse = new IssueListResponse();
        List<Issue> issues = issueRepository.getIssuesByProject(projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found")));

        issueListResponse.addAllIssues(issues);

        return issueListResponse;
    }

    //할당된 이슈 검색 (DEV)
    public IssueListResponse findIssueAssignedTo(Long userId) { //user가 developer가 맞는지 확인하는 과정 필요
        IssueListResponse issueListResponse = new IssueListResponse();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Issue> issues = issueRepository.getIssuesByAssignee(user);

        issueListResponse.addAllIssues(issues);

        return issueListResponse;
    }

    //이슈 상태 변경 (PL, DEV, TESTER)
    public IssueResponse setIssueState(Long id, IssueStateRequest issueStateRequest) {
        Issue issue = issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        try {
            issue.setState(issueStateRequest.getState());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        issueRepository.save(issue); // 변경된 상태 저장

        return new IssueResponse(issueRepository.getById(id));
    }


    //이슈 할당 (PL)
    public IssueResponse setIssueAssignee(Long id, IssueAssigneeRequest issueAssigneeRequest) {
        User user = userRepository.findUserByUserId(issueAssigneeRequest.getUserId());
        Issue issue = issueRepository.getById(id);

        try {
            issue.setAssignee(user);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        issue.setState(IssueState.ASSIGNED);
        issueRepository.save(issue);

        return new IssueResponse(issue);
    }
}
