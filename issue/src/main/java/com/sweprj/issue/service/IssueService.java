package com.sweprj.issue.service;

import com.sweprj.issue.DTO.IssueRequestDTO;
import com.sweprj.issue.DTO.IssueResponseDTO;
import com.sweprj.issue.DTO.IssueStateRequestDTO;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.IssueAssignee;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.enums.IssueState;
import com.sweprj.issue.repository.IssueAssigneeRepository;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public IssueResponseDTO createIssue(Long projectId, User reporter, IssueRequestDTO issueRequestDTO) {
        Issue issue = new Issue();

        issue.setTitle(issueRequestDTO.getTitle());
        issue.setDescription(issueRequestDTO.getDescription());
        issue.setReporter(reporter);
        issue.setPriority(issueRequestDTO.getPriority());
        issue.setProject(projectRepository.getById(projectId));
        issue.setReportedAt(new Date());

        issueRepository.save(issue);

        return new IssueResponseDTO(issue);
    }

    //모든 이슈 검색
    public List<IssueResponseDTO> findAll() {
        List<IssueResponseDTO> issueResponseDTOS = new ArrayList<>();
        List<Issue> issues = issueRepository.findAll();

        for (int i = 0; i < issues.size(); i++) {
            issueResponseDTOS.add(new IssueResponseDTO(issues.get(i)));
        }

        return issueResponseDTOS;
    }

    //이슈 상세정보 확인
    public IssueResponseDTO findDTOById(Long id) {
        return new IssueResponseDTO(issueRepository.getById(id));
    }

    public Issue findById(Long id) {
        return issueRepository.getById(id);
    }


    //프로젝트 전체 이슈 검색
    public List<IssueResponseDTO> findByProject(Long projectId) {
        List<IssueResponseDTO> issueResponseDTOS = new ArrayList<>();
        List<Issue> issues = issueRepository.getIssuesByProject(projectRepository.getById(projectId));

        for (int i = 0; i < issues.size(); i++) {
            issueResponseDTOS.add(new IssueResponseDTO(issues.get(i)));
        }

        return issueResponseDTOS;
    }

    //할당된 이슈 검색
    public List<IssueResponseDTO> findIssueAssignedTo(Long userId) { //user가 developer가 맞는지 확인하는 과정 필요
        List<IssueResponseDTO> issueResponseDTOS = new ArrayList<>();
        List<IssueAssignee>  issueAssignees = issueAssigneeRepository.getIssueAssigneesByAssignee(userRepository.findUserByUserId(userId));

        for (int i = 0; i < issueAssignees.size(); i++) {
            issueResponseDTOS.add(new IssueResponseDTO(issueAssignees.get(0).getIssue()));
        }

        return issueResponseDTOS;
    }

    //이슈 상태 변경
    public IssueResponseDTO setIssueState(Long id, IssueStateRequestDTO issueStateRequestDTO) {
        Issue issue = issueRepository.getById(id);
        try {
            issue.setState(issueStateRequestDTO.getState());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        issueRepository.save(issue); // 변경된 상태 저장

        return new IssueResponseDTO(issueRepository.getById(id));
    }
}
