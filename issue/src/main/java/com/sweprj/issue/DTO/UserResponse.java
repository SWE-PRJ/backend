package com.sweprj.issue.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String identifier;
    private List<Long> issues;
    private List<Long> comments;
    private List<Long> projects;
    private String role;

}