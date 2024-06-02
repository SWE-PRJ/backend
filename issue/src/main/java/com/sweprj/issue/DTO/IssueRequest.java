package com.sweprj.issue.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IssueRequest {
    private String title;
    private String description;
    private String priority;
 }
