package com.sweprj.issue.service;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.enums.IssuePriority;
import com.sweprj.issue.domain.enums.IssueState;
import com.sweprj.issue.exception.InvalidIssuePriorityException;
import com.sweprj.issue.exception.InvalidIssueStateException;
import com.sweprj.issue.exception.ResourceNotFoundException;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.ProjectRepository;
import com.sweprj.issue.repository.ProjectUserRepository;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IssueService {

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ProjectUserRepository projectUserRepository;

    public IssueService(ProjectRepository projectRepository, IssueRepository issueRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, ProjectUserRepository projectUserRepository) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.projectUserRepository = projectUserRepository;
    }

    //이슈 생성 (TESTER)
    public IssueResponse createIssue(Long projectId, IssueRequest issueRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) authentication.getCredentials();
        Long userId = jwtTokenProvider.getUserFromJwt(token);

        Issue issue = new Issue();

        if (!IssuePriority.isValid(issueRequest.getPriority())) {
            throw new InvalidIssuePriorityException(issueRequest.getPriority() + "는 잘못된 이슈 우선순위입니다.");
        }

        issue.setTitle(issueRequest.getTitle());
        issue.setDescription(issueRequest.getDescription());
        issue.setReporter(userRepository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException("User not found")));
        issue.setPriority(IssuePriority.fromString(issueRequest.getPriority()));
        issue.setProject(projectRepository.findById(projectId).orElseThrow(()
                -> new ResourceNotFoundException("Project not found")));
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
    public IssueListResponse findIssueAssignedTo(String userIdentifier) { //user가 developer가 맞는지 확인하는 과정 필요
        IssueListResponse issueListResponse = new IssueListResponse();
        User user = userRepository.findByIdentifier(userIdentifier).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Issue> issues = issueRepository.getIssuesByAssignee(user);

        issueListResponse.addAllIssues(issues);

        return issueListResponse;
    }

    //이슈 상태 변경 (PL, DEV, TESTER)
    public IssueResponse setIssueState(Long id, IssueStateRequest issueStateRequest) {
        Issue issue = issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));

        if (!IssueState.isValid(issueStateRequest.getState())) {
            throw new InvalidIssueStateException(issueStateRequest.getState() + "는 잘못된 이슈 상태입니다.");
        }
        issue.setState(IssueState.fromString(issueStateRequest.getState()));
        issueRepository.save(issue); // 변경된 상태 저장

        return new IssueResponse(issueRepository.getById(id));
    }


    //이슈 할당 (PL)
    public IssueResponse setIssueAssignee(Long id, IssueAssigneeRequest issueAssigneeRequest) {
        Optional<User> user = userRepository.findByIdentifier(issueAssigneeRequest.getUserIdentifier());
        Issue issue = issueRepository.getById(id);
        Project project = issue.getProject();

        if (user == null) {
            throw new ResourceNotFoundException("해당 id를 가진 유저가 없습니다.");
        }

        if (projectUserRepository.getProjectUserByProjectAndUser(project, user.get()) == null) {
            throw new ResourceNotFoundException("해당 유저는 해당 이슈가 발생된 프로젝트에 속하지 않았습니다.");
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
