package com.sweprj.issue.service;

import com.sweprj.issue.DTO.CommentDTO;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.domain.Comment;
import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.exception.ResourceNotFoundException;
import com.sweprj.issue.repository.CommentRepository;
import com.sweprj.issue.repository.IssueRepository;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public CommentService(CommentRepository commentRepository, IssueRepository issueRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public CommentDTO createComment(Long issueId, Long userId, String content) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setIssue(issue);
        comment.setCommenter(user);
        comment.setContent(content);
        comment.setCommentedAt(new Date());

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    public CommentDTO getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        return convertToDTO(comment);
    }

    public CommentDTO updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId));
        checkIfAuthorized(comment);
        if (content != null) {
            comment.setContent(content);
        }
        Comment updatedComment = commentRepository.save(comment);
        return convertToDTO(updatedComment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        checkIfAuthorized(comment);
        commentRepository.delete(comment);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setCommenterId(comment.getCommenter().getUserId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCommentedAt(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(comment.getCommentedAt()));
        commentDTO.setIssueId(comment.getIssue().getId());
        return commentDTO;
    }

    private void checkIfAuthorized(Comment comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long userId = jwtTokenProvider.getUserFromJwt(token);
        String role = jwtTokenProvider.getRoleFromJwt(token);
        if (!role.equals("ADMIN") && !comment.getCommenter().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("You are not authorized to perform this action");
        }
    }
}
