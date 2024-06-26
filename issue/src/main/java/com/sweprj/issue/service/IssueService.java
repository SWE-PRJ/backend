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
import com.sweprj.issue.exception.UnauthorizedException;
import com.sweprj.issue.repository.*;
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
    private final IssueEmbeddingRepository issueEmbeddingRepository;
    private final EmbeddingService embeddingService;

    public IssueService(ProjectRepository projectRepository, IssueRepository issueRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, ProjectUserRepository projectUserRepository, IssueEmbeddingRepository issueEmbeddingRepository, EmbeddingService embeddingService) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.projectUserRepository = projectUserRepository;
        this.issueEmbeddingRepository = issueEmbeddingRepository;
        this.embeddingService = embeddingService;
    }

    private void checkingInProject(Long projectId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) authentication.getCredentials();
        Long userId = jwtTokenProvider.getUserFromJwt(token);
        User user = userRepository.getReferenceById(userId);
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new ResourceNotFoundException("Project not found"));

        if (user.getRole() == "ROLE_ADMIN") {
            return;
        }

        if (projectUserRepository.getProjectUserByProjectAndUser(project, user) == null) {
            throw new UnauthorizedException("해당 유저는 해당 프로젝트에 소속되지 않았습니다.");
        }
    }

    //이슈 생성 (TESTER)
    public IssueResponse createIssue(Long projectId, IssueRequest issueRequest) {
        checkingInProject(projectId);
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

    public IssueListResponse findByProjectAndState(Long projectId, String stateString) {
        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new ResourceNotFoundException("User not found"));

        if (!IssueState.isValid(stateString)) {
            throw new InvalidIssueStateException(stateString + "는 정상적인 state가 아닙니다.");
        }
        IssueState state = IssueState.fromString(stateString);
        List<Issue> issues = issueRepository.getIssuesByProjectAndState(project, state);

        IssueListResponse issueListResponse = new IssueListResponse();
        issueListResponse.addAllIssues(issues);
        return issueListResponse;
    }

    //이슈 상세정보 확인 (PL, DEV, TESTER)
    public IssueResponse findDTOById(Long id) {
        Issue issue = findById(id);
        checkingInProject(issue.getProject().getId());
        return new IssueResponse(issue);
    }

    public Issue findById(Long id) {
        return issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
    }


    //프로젝트 전체 이슈 검색 (PL)
    public IssueListResponse findByProject(Long projectId) {
        checkingInProject(projectId);
        IssueListResponse issueListResponse = new IssueListResponse();
        List<Issue> issues = issueRepository.getIssuesByProject(projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found")));

        issueListResponse.addAllIssues(issues);

        return issueListResponse;
    }

    //할당된 이슈 검색 (DEV)
    public IssueListResponse findIssuesAssignedTo(Long projectId, String userIdentifier) {
        checkingInProject(projectId);

        IssueListResponse issueListResponse = new IssueListResponse();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User user = userRepository.findByIdentifier(userIdentifier).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Issue> issues = issueRepository.getIssuesByProjectAndAssignee(project, user);

        issueListResponse.addAllIssues(issues);

        return issueListResponse;
    }

    // 제안한 이슈 검색 (TESTER)
    public IssueListResponse findIssuesReportedBy(Long projectId, String userIdentifier) {
        checkingInProject(projectId);

        IssueListResponse issueListResponse = new IssueListResponse();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User user = userRepository.findByIdentifier(userIdentifier).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Issue> issues = issueRepository.getIssuesByProjectAndReporter(project, user);

        issueListResponse.addAllIssues(issues);

        return issueListResponse;
    }

    //이슈 상태 변경 (PL, DEV, TESTER)
    public IssueResponse setIssueState(Long id, IssueStateRequest issueStateRequest) {
        Issue issue = issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        checkingInProject(issue.getProject().getId());

        if (!IssueState.isValid(issueStateRequest.getState())) {
            throw new InvalidIssueStateException(issueStateRequest.getState() + "는 잘못된 이슈 상태입니다.");
        }
        if (IssueState.FIXED == IssueState.fromString(issueStateRequest.getState())) {

            User fixer = issue.getAssignee();
            issue.setFixer(fixer);
            embeddingService.createIssueEmbedding(issue, fixer);
        }

        issue.setState(IssueState.fromString(issueStateRequest.getState()));
        issueRepository.save(issue); // 변경된 상태 저장

        return new IssueResponse(issueRepository.getById(id));
    }


    //이슈 할당 (PL)
    public IssueResponse setIssueAssignee(Long id, IssueAssigneeRequest issueAssigneeRequest) {
        Issue issue = issueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        Project project = issue.getProject();
        checkingInProject(project.getId());

        Optional<User> user = userRepository.findByIdentifier(issueAssigneeRequest.getUserIdentifier());
        if (user == null) {
            throw new ResourceNotFoundException("해당 id를 가진 유저가 없습니다.");
        }
        if (user.get() == issue.getAssignee()) {
            return new IssueResponse(issue);
        }

        issue.setAssignee(user.get());
        issue.setState(IssueState.ASSIGNED);
        issueRepository.save(issue);

        return new IssueResponse(issue);
    }

    //이슈 통계
    public IssueStatisticsDTO getIssueStatistics(Long projectId, Date start, Date end) {
        IssueStatisticsDTO stats = new IssueStatisticsDTO();

        long totalIssues = issueRepository.countAllIssuesWithinDateRange(projectId, start, end);
        stats.setTotalIssues(totalIssues);

        List<Object[]> issuesByState = issueRepository.countIssuesByState(projectId, start, end);
        Map<String, Long> issuesByStateMap = new HashMap<>();
        for (Object[] row : issuesByState) {
            issuesByStateMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setIssuesByStatus(issuesByStateMap);

        List<Object[]> issuesByPriority = issueRepository.countIssuesByPriority(projectId, start, end);
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

    public Map<String, Boolean> deleteIssue(Long issueId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) authentication.getCredentials();
        Long userId = jwtTokenProvider.getUserFromJwt(token);
        User requestUser = userRepository.getReferenceById(userId);
        Issue issue = findById(issueId);
        checkingInProject(issue.getProject().getId());

        if (requestUser.getRole() == "ROLE_ADMIN" || requestUser.getRole() == "ROLE_PL" || requestUser == issue.getReporter()) {

            issueEmbeddingRepository.deleteIssueEmbeddingByIssue(issue.getId());
            issueRepository.delete(issue);

            Map<String,Boolean> m = new HashMap<>();
            m.put("onSuccess", true);
            return m;
        }

        throw new UnauthorizedException("해당 유저는 해당 이슈 삭제에 대한 권한이 없습니다.");
    }
}