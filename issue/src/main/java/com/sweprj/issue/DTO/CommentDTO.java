package com.sweprj.issue.DTO;

import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private String commenterIdentifier;
    private String content;
    private String commentedAt;
    private Long issueId;
}
