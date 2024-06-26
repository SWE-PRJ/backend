package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.exception.ResourceNotFoundException;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.UserRepository;
import com.sweprj.issue.service.DevRecommendationService;
import com.sweprj.issue.service.EmbeddingService;
import com.sweprj.issue.service.IssueService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;
    private final IssueRepository issueRepository;
    private final EmbeddingService embeddingService;
    private final DevRecommendationService recommendationService;
    private final UserRepository userRepository;

    public IssueController(IssueService issueService, IssueRepository issueRepository, EmbeddingService embeddingService, DevRecommendationService recommendationService, UserRepository userRepository) {
        this.issueService = issueService;
        this.issueRepository = issueRepository;
        this.embeddingService = embeddingService;
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    //프로젝트에서 이슈 생성 (TESTER)
    @PostMapping("/projects/{projectId}/issues")
    @ResponseBody
    public ResponseEntity<IssueResponse> createIssue(@PathVariable("projectId") Long projectId, @RequestBody IssueRequest issueRequest) {
        return ResponseEntity.ok(issueService.createIssue(projectId, issueRequest));
    }

    //프로젝트의 전체 이슈 검색 (PL)
    @GetMapping("/projects/{projectId}/issues")
    @ResponseBody
    public ResponseEntity<IssueListResponse> findIssuesIn(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(issueService.findByProject(projectId));
    }

    @GetMapping("/projects/{projectId}/issues/{state}")
    @ResponseBody
    public ResponseEntity<IssueListResponse> findIssuesIn(@PathVariable("projectId") Long projectId, @PathVariable("state") String state) {
        return ResponseEntity.ok(issueService.findByProjectAndState(projectId, state));
    }

    //유저에게 할당된 이슈 검색 (DEV)
    @GetMapping("/projects/{projectId}/developers/{userIdentifier}/issues")
    @ResponseBody
    public ResponseEntity<IssueListResponse> browseAssignedIssues(@PathVariable("projectId") Long projectId, @PathVariable("userIdentifier") String userIdentifier) {
        return ResponseEntity.ok(issueService.findIssuesAssignedTo(projectId, userIdentifier));
    }

    //유저가 제안한 이슈 검색 (TESTER)
    @GetMapping("/projects/{projectId}/testers/{userIdentifier}/issues")
    @ResponseBody
    public ResponseEntity<IssueListResponse> browseReportedIssues(@PathVariable("projectId") Long projectId, @PathVariable("userIdentifier") String userIdentifier) {
        return ResponseEntity.ok(issueService.findIssuesReportedBy(projectId, userIdentifier));
    }

    //이슈 상세정보 확인 (PL, DEV, TESTER)
    @GetMapping("/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<IssueResponse> getIssue(@PathVariable("issueId") Long id) {
        return ResponseEntity.ok(issueService.findDTOById(id));
    }

    //이슈 상태 변경 (PL, DEV, TESTER)
    @PatchMapping("/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<IssueResponse> changeIssue(@PathVariable("issueId") Long id, @RequestBody IssueStateRequest issueStateRequest) {
        Issue issue = issueService.findById(id);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(issueService.setIssueState(id, issueStateRequest));
    }

    //이슈 할당 (PL)
    @PostMapping("/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<IssueResponse> assigneIssue(@PathVariable("issueId") Long id, @RequestBody IssueAssigneeRequest issueAssigneeRequest) {
        Issue issue = issueService.findById(id);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(issueService.setIssueAssignee(id, issueAssigneeRequest));
    }

    @GetMapping("/projects/{projectId}/issues/statistics")
    public ResponseEntity<IssueStatisticsDTO> getIssueStatistics(
            @PathVariable Long projectId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        try {
            Date start = formatter.parse(startDate);
            Date end = formatter.parse(endDate);
            IssueStatisticsDTO stats = issueService.getIssueStatistics(projectId, start, end);
            return ResponseEntity.ok(stats);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/issues/{issueId}/recommend")
    public ResponseEntity<UserRecommendDTO> recommendDeveloper(@PathVariable Long issueId) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new RuntimeException("Issue not found."));
        User recommendedUser = recommendationService.recommendDeveloperForIssue(issue);
        if (recommendedUser == null) {
            throw new ResourceNotFoundException("No recommended developer found.");
        }
        UserRecommendDTO userRecommendDTO = new UserRecommendDTO(recommendedUser.getUserId(), recommendedUser.getUsername());
        return ResponseEntity.ok(userRecommendDTO);
    }

    @DeleteMapping("/issues/{issueId}")
    public ResponseEntity<Map<String, Boolean>> deleteIssue(@PathVariable Long issueId) {
        return ResponseEntity.ok(issueService.deleteIssue(issueId));
    }
}
