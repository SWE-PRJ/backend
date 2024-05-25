package com.sweprj.issue.DTO;

import lombok.Getter;

@Getter
public class IssueRequestDTO {
    private String title;
    private String description;
    private Long  reporterId;
    private String priority;
}
