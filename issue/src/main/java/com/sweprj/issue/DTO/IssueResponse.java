package com.sweprj.issue.DTO;

import com.sweprj.issue.domain.Comment;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.enums.IssuePriority;
import com.sweprj.issue.domain.enums.IssueState;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class IssueResponse {

    public IssueResponse(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("Issue cannot be null");
        }

        id = issue.getId();
        title = issue.getTitle();
        description = issue.getDescription();

        if (issue.getReporter() == null) {
            reporterName = ""; // 또는 적절한 기본값 설정
        } else {
            reporterName = issue.getReporter().getUsername();
        }

        if (issue.getFixer() == null) {
            fixerName = ""; // 또는 적절한 기본값 설정
        } else {
            fixerName = issue.getFixer().getUsername();
        }

        assigneeName = issue.getAssignee() != null ? issue.getAssignee().getIdentifier() : "";
        priority = issue.getPriority() != null ? issue.getPriority() : null; // 필요 시 기본값 설정
        state = issue.getState() != null ? issue.getState() : IssueState.NEW; // 필요 시 기본값 설정
        projectId = issue.getProject() != null ? issue.getProject().getId() : -1L;
        reportedAt = issue.getReportedAt() != null ? issue.getReportedAt() : null; // 필요 시 기본값 설정
        comments = issue.getComments() != null ? issue.getComments() : Collections.emptyList();
    }

    private Long id;
    private String title;
    private String description;
    private String reporterName;
    private String fixerName;
    private String assigneeName;
    private IssuePriority priority;
    private IssueState state;
    private Long projectId;
    private Date reportedAt;
    private List<Comment> comments;
}
