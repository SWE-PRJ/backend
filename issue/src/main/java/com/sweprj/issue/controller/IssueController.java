package com.sweprj.issue.controller;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.service.IssueService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("api/projects/{projectId}/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/create")
    public ResponseEntity<Issue> createIssue(@PathVariable("projectId") Long projectId, @RequestParam String title, @RequestParam String description) {
        Issue issue = issueService.createIssue(projectId, title, description);
        return ResponseEntity.ok(issue);
    }

    @GetMapping("")
    @ResponseBody
    public ResponseEntity<List<Issue>> findAllIn(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(issueService.findAllIn(projectId));
    }

    @GetMapping("")
    @ResponseBody
    public ResponseEntity<List<Issue>> findByState(@PathVariable("projectId") Long projectId, @RequestParam("state") String state) {
        return ResponseEntity.ok(issueService.findByState(projectId, state));
    }

    @PatchMapping("/{issueId}")
    @ResponseBody
    public ResponseEntity<Optional<Issue>> changeIssue(@PathVariable("issueId") Long id, @RequestParam("state") String state) {
        return ResponseEntity.ok(issueService.findById(id));
    }
}
