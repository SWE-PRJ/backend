package com.sweprj.issue.repository;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.IssueEmbedding;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueEmbeddingRepository extends JpaRepository<IssueEmbedding, Long> {
    List<IssueEmbedding> findByUserUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE from IssueEmbedding IE where IE.issue.id = :issue_id")
    void deleteIssueEmbeddingByIssue(Long issue_id);
}
