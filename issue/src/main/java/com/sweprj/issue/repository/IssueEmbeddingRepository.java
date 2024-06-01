package com.sweprj.issue.repository;

import com.sweprj.issue.domain.IssueEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueEmbeddingRepository extends JpaRepository<IssueEmbedding, Long> {
    List<IssueEmbedding> findByUserUserId(Long userId);
}
