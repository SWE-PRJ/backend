package com.sweprj.issue.controller;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.service.IssueService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    //프로젝트에서 이슈 생성
    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<Issue> createIssue(@PathVariable("projectId") Long projectId, @RequestParam String title, @RequestParam String description) {
        Issue issue = issueService.createIssue(projectId, title, description);
        return ResponseEntity.ok(issue);
    }

    //프로젝트의 전체 이슈 검색
    @GetMapping("/projects/{projectId}/issues")
    @ResponseBody
    public ResponseEntity<List<Issue>> findIssuesIn(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(issueService.findByProject(projectId));
    }

    //유저에게 할당된 이슈 검색
    @GetMapping("/users/{userId}/issues")
    @ResponseBody
    public ResponseEntity<List<Issue>> browseAssignedIssues(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(issueService.findIssueAssignedTo(userId));
    }

    //이슈 상세정보 확인
    @GetMapping("/api/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<Issue> getIssue(@PathVariable("issueId") Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    //이슈 상태 변경
    @PatchMapping("/projects/{projectId}/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<Issue> changeIssue(@PathVariable("issueId") Long id, @RequestParam("state") String state) {
        return ResponseEntity.ok(issueService.setIssueState(id, state));
    }
}
