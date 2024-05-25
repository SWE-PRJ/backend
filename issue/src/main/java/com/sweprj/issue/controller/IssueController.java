package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.IssueRequestDTO;
import com.sweprj.issue.DTO.IssueResponseDTO;
import com.sweprj.issue.DTO.IssueStateRequestDTO;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.service.IssueService;
import com.sweprj.issue.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public ResponseEntity<IssueResponseDTO> createIssue(@PathVariable("projectId") Long projectId, @RequestBody IssueRequestDTO issueRequestDTO) {
        User reporter = userService.findById(issueRequestDTO.getReporterId());
        if (reporter == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(issueService.createIssue(projectId, reporter, issueRequestDTO));
    }

    //프로젝트의 전체 이슈 검색
    @GetMapping("/projects/{projectId}/issues")
    @ResponseBody
    public ResponseEntity<List<IssueResponseDTO>> findIssuesIn(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(issueService.findByProject(projectId));
    }

    //유저에게 할당된 이슈 검색
    @GetMapping("/users/{userId}/issues")
    @ResponseBody
    public ResponseEntity<List<IssueResponseDTO>> browseAssignedIssues(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(issueService.findIssueAssignedTo(userId));
    }

    //이슈 상세정보 확인
    @GetMapping("/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<IssueResponseDTO> getIssue(@PathVariable("issueId") Long id) {
        return ResponseEntity.ok(issueService.findDTOById(id));
    }

    //이슈 상태 변경
    @PatchMapping("/projects/{projectId}/issues/{issueId}")
    @ResponseBody
    public ResponseEntity<IssueResponseDTO> changeIssue(@PathVariable("issueId") Long id, @RequestBody IssueStateRequestDTO issueStateRequestDTO) {
        Issue issue = issueService.findById(id);
        if (issue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(issueService.setIssueState(id, issueStateRequestDTO));
    }
}
