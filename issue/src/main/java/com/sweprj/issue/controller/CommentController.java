package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.CommentDTO;
import com.sweprj.issue.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/issues/{issueId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long issueId, @RequestBody Map<String, Object> requestBody) {
        Long userId = Long.valueOf(requestBody.get("userId").toString());
        String content = requestBody.get("content").toString();

        CommentDTO comment = commentService.createComment(issueId, userId, content);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long issueId, @PathVariable Long commentId) {
        CommentDTO comment = commentService.getComment(commentId);
        return ResponseEntity.ok(comment);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long issueId, @PathVariable Long commentId, @RequestBody Map<String, String> requestBody) {
        String content = requestBody.get("content");
        CommentDTO comment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long issueId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }
}
