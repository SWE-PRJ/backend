package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.UserRepository;
import com.sweprj.issue.service.DevRecommendationService;
import com.sweprj.issue.service.EmbeddingService;
import com.sweprj.issue.service.IssueService;
import com.sweprj.issue.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;
    private final IssueRepository issueRepository;
    private final EmbeddingService embeddingService;
    private final DevRecommendationService recommendationService;
    private final UserRepository userRepository;

    public IssueController(IssueService issueService, UserService userService, IssueRepository issueRepository, EmbeddingService embeddingService, DevRecommendationService recommendationService, UserRepository userRepository) {
        this.issueService = issueService;
        this.userService = userService;
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

    //유저에게 할당된 이슈 검색 (DEV)
    @GetMapping("/users/{userIdentifier}/issues")
    @ResponseBody
    public ResponseEntity<IssueListResponse> browseAssignedIssues(@PathVariable("userIdentifier") String userIdentifier) {
        return ResponseEntity.ok(issueService.findIssueAssignedTo(userIdentifier));
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
        User user = userRepository.findByIdentifier(issueAssigneeRequest.getUserIdentifier()).get();
        embeddingService.createIssueEmbedding(issue, user);
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
        UserRecommendDTO userRecommendDTO = new UserRecommendDTO(recommendedUser.getUserId(), recommendedUser.getUsername());
        return ResponseEntity.ok(userRecommendDTO);
    }

}
