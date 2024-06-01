package com.sweprj.issue.DTO;

import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long commenterId;
    private String content;
    private String commentedAt;
    private Long issueId;
}
