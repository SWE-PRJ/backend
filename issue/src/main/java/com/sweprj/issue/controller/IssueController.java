package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.IssueRequestDTO;
import com.sweprj.issue.DTO.IssueResponseDTO;
import com.sweprj.issue.DTO.IssueStateRequestDTO;
import com.sweprj.issue.DTO.IssueStatisticsDTO;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.service.IssueService;
import com.sweprj.issue.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        System.out.println(issueStateRequestDTO.getState());
        return ResponseEntity.ok(issueService.setIssueState(id, issueStateRequestDTO));
    }

    @GetMapping("/projects/{projectId}/issues/statistics")
    public ResponseEntity<IssueStatisticsDTO> getIssueStatistics(
            @PathVariable Long projectId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start = null;
        Date end = null;
        Calendar cal = Calendar.getInstance();

        try {
            if (startDate != null) {
                start = formatter.parse(startDate);
            }
            if (endDate != null) {
                end = formatter.parse(endDate);
            }

            if (start == null && end == null) {
                end = new Date();
                cal.setTime(end);
                cal.add(Calendar.DAY_OF_YEAR, -7);
                start = cal.getTime();
            } else if (start == null) {
                cal.setTime(end);
                cal.add(Calendar.DAY_OF_YEAR, -7);
                start = cal.getTime();
            } else if (end == null) {
                cal.setTime(start);
                cal.add(Calendar.DAY_OF_YEAR, 7);
                end = cal.getTime();
            }
        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }

        IssueStatisticsDTO stats = issueService.getIssueStatistics(projectId, start, end);
        return ResponseEntity.ok(stats);
    }
}
