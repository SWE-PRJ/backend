package com.sweprj.issue.service;

import com.sweprj.issue.domain.Developer;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.IssueAssignee;
import com.sweprj.issue.domain.enums.IssueState;
import com.sweprj.issue.repository.IssueAssigneeRepository;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IssueService {

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final IssueAssigneeRepository issueAssigneeRepository;
    private final UserRepository userRepository;

    public IssueService(ProjectRepository projectRepository, IssueRepository issueRepository, IssueAssigneeRepository issueAssigneeRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.issueAssigneeRepository = issueAssigneeRepository;
        this.userRepository = userRepository;
    }

    //이슈 생성
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

    //모든 이슈 검색
    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    //이슈 상세정보 확인
    public Issue findById(Long id) {
        return issueRepository.getById(id);
    }

    //프로젝트 전체 이슈 검색
    public List<Issue> findByProject(Long projectId) {
        return issueRepository.getIssuesByProject(projectRepository.getById(projectId));
    }

    //할당된 이슈 검색
    public List<Issue> findIssueAssignedTo(Long userId) { //user가 developer가 맞는지 확인하는 과정 필요
        List<IssueAssignee>  issueAssigneeList = issueAssigneeRepository.getIssueAssigneesByAssignee(userRepository.findUserByUserId(userId));
        List<Issue> issueList = new ArrayList<>();

        for (Integer i = 0; i < issueAssigneeList.size(); i++) {
            issueList.add(issueAssigneeList.get(0).getIssue());
        }

        return issueList;
    }

    //이슈 상태 변경
    public Issue setIssueState(Long id, String stateString) {
        Issue issue = issueRepository.getById(id);

        try {
            IssueState state = IssueState.fromString(stateString);
            issue.setState(state);
            System.out.println("State set to: " + issue.getState());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        return issue;
    }
}
