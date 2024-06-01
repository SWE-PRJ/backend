package com.sweprj.issue.service;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.DTO.IssueStatisticsDTO;
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
import java.util.*;

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
    public IssueResponse createIssue(Long projectId, IssueRequest issueRequest) {
        Optional<User> reporter = userRepository.findByIdentifier(issueRequest.getReporterIdentifier());

        if (reporter == null) {
            throw new ResourceNotFoundException("해당 id를 가진 유저가 없습니다.");
        }

        Issue issue = new Issue();

        issue.setTitle(issueRequest.getTitle());
        issue.setDescription(issueRequest.getDescription());
        issue.setReporter(reporter.get());
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
        Optional<User> user = userRepository.findByIdentifier(issueAssigneeRequest.getIdentifier());
        Issue issue = issueRepository.getById(id);

        if (user == null) {
            throw new ResourceNotFoundException("해당 id를 가진 유저가 없습니다.");
        }

        issue.setAssignee(user.get());
        issue.setState(IssueState.ASSIGNED);
        issueRepository.save(issue);

        return new IssueResponse(issue);
    }

    //이슈 통계
    public IssueStatisticsDTO getIssueStatistics(Long projectId, Date start, Date end) {
        IssueStatisticsDTO stats = new IssueStatisticsDTO();

        long totalIssues = issueRepository.count();
        stats.setTotalIssues(totalIssues);

        List<Object[]> issuesByState = issueRepository.countIssuesByState(projectId);
        Map<String, Long> issuesByStateMap = new HashMap<>();
        for (Object[] row : issuesByState) {
            issuesByStateMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setIssuesByStatus(issuesByStateMap);

        List<Object[]> issuesByPriority = issueRepository.countIssuesByPriority(projectId);
        Map<String, Long> issuesByPriorityMap = new HashMap<>();
        for (Object[] row : issuesByPriority) {
            issuesByPriorityMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setIssuesByPriority(issuesByPriorityMap);

        // Calculate monthly statistics
        List<Object[]> issuesByMonth = issueRepository.countIssuesByMonth(projectId, start, end);
        Map<String, Long> issuesByMonthMap = new HashMap<>();
        for (Object[] row : issuesByMonth) {
            issuesByMonthMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setIssuesByMonth(issuesByMonthMap);

        // Calculate daily statistics per month
        List<Object[]> issuesByDayPerMonth = issueRepository.countIssuesByDayPerMonth(projectId, start, end);
        Map<String, Map<String, Long>> issuesByDayPerMonthMap = new HashMap<>();
        for (Object[] row : issuesByDayPerMonth) {
            String month = row[0].toString();
            String day = row[1].toString();
            Long count = (Long) row[2];
            if (!issuesByDayPerMonthMap.containsKey(month)) {
                issuesByDayPerMonthMap.put(month, new HashMap<>());
            }
            issuesByDayPerMonthMap.get(month).put(day, count);
        }
        stats.setIssuesByDayPerMonth(issuesByDayPerMonthMap);

        return stats;
    }
}
