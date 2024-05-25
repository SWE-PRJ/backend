package com.sweprj.issue.DTO;

import com.sweprj.issue.domain.Comment;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.enums.IssueState;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class IssueResponseDTO {

    public IssueResponseDTO(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("Issue cannot be null");
        }

        id = issue.getId();
        title = issue.getTitle();
        description = issue.getDescription();

        if (issue.getReporter() == null) {
            reporterId = -1L; // 또는 적절한 기본값 설정
        } else {
            reporterId = Long.valueOf(issue.getReporter().getUserId());
        }

        if (issue.getFixer() == null) {
            fixerId = -1L; // 또는 적절한 기본값 설정
        } else {
            fixerId = issue.getFixer().getUserId() != null ? issue.getFixer().getUserId() : -1L;
        }

        priority = issue.getPriority() != null ? issue.getPriority() : null; // 필요 시 기본값 설정
        state = issue.getState() != null ? issue.getState() : IssueState.NEW; // 필요 시 기본값 설정
        projectId = issue.getProject() != null ? issue.getProject().getId() : -1L;
        reportedAt = issue.getReportedAt() != null ? issue.getReportedAt() : null; // 필요 시 기본값 설정
        comments = issue.getComments() != null ? issue.getComments() : Collections.emptyList();
    }

    private Long id;
    private String title;
    private String description;
    private Long reporterId;
    private Long fixerId;
    private String priority;
    private IssueState state;
    private Long projectId;
    private Date reportedAt;
    private List<Comment> comments;
}
