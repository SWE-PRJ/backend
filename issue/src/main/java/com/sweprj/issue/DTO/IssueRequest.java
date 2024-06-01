package com.sweprj.issue.DTO;

import com.sweprj.issue.domain.enums.IssuePriority;
import lombok.Getter;

@Getter
public class IssueRequest {
    private String title;
    private String description;
    private Long  reporterId;
    private IssuePriority priority;
}
