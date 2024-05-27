package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.*;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.service.IssueService;
import com.sweprj.issue.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;

    public IssueController(IssueService issueService, UserService userService) {
        this.issueService = issueService;
        this.userService = userService;
    }

    //프로젝트에서 이슈 생성
    @PostMapping("/projects/{projectId}/issues")
    @ResponseBody
    public ResponseEntity<IssueResponse> createIssue(@PathVariable("projectId") Long projectId, @RequestBody IssueRequest issueRequest) {
        User reporter = userService.findById(issueRequest.getReporterId());
        if (reporter == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(issueService.createIssue(projectId, reporter, issueRequest));
    }

    //프로젝트의 전체 이슈 검색
    @GetMapping("/projects/{projectId}/issues")
    @ResponseBody
    public ResponseEntity<IssueListResponse> findIssuesIn(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(issueService.findByProject(projectId));
    }

    //유저에게 할당된 이슈 검색
    @GetMapping("/users/{userId}/issues")
    @ResponseBody
    public ResponseEntity<IssueListResponse> browseAssignedIssues(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(issueService.findIssueAssignedTo(userId));
    }

    //이슈 상세정보 확인
    @GetMapping("/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<IssueResponse> getIssue(@PathVariable("issueId") Long id) {
        return ResponseEntity.ok(issueService.findDTOById(id));
    }

    //이슈 상태 변경
    @PatchMapping("/projects/{projectId}/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<IssueResponse> changeIssue(@PathVariable("issueId") Long id, @RequestBody IssueStateRequest issueStateRequest) {
        Issue issue = issueService.findById(id);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(issueService.setIssueState(id, issueStateRequest));
    }

    //이슈 할당
    @PostMapping("/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<IssueResponse> assigneIssue(@PathVariable("issueId") Long id, @RequestBody IssueAssigneeRequest issueAssigneeRequest) {
        Issue issue = issueService.findById(id);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(issueService.setIssueAssignee(issue, issueAssigneeRequest));
    }
}
