package com.sweprj.issue.repository;

import com.sweprj.issue.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findAllIn(Long projectId);
    List<Issue> findByState(Long projectId, String state);
    Optional<Issue> findById(Long id);
}
