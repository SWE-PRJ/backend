package com.sweprj.issue.repository;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> getIssuesByProject(Project project);

    @Query("SELECT i.state, COUNT(i) FROM Issue i WHERE i.project.id = :projectId GROUP BY i.state")
    List<Object[]> countIssuesByState(Long projectId);

    @Query("SELECT i.priority, COUNT(i) FROM Issue i WHERE i.project.id = :projectId GROUP BY i.priority")
    List<Object[]> countIssuesByPriority(Long projectId);

    @Query("SELECT MONTH(i.reportedAt), COUNT(i) FROM Issue i WHERE i.project.id = :projectId AND i.reportedAt BETWEEN :startDate AND :endDate GROUP BY MONTH(i.reportedAt)")
    List<Object[]> countIssuesByMonth(Long projectId, Date startDate, Date endDate);

    @Query("SELECT MONTH(i.reportedAt), DAY(i.reportedAt), COUNT(i) FROM Issue i WHERE i.project.id = :projectId AND i.reportedAt BETWEEN :startDate AND :endDate GROUP BY MONTH(i.reportedAt), DAY(i.reportedAt)")
    List<Object[]> countIssuesByDayPerMonth(Long projectId, Date startDate, Date endDate);
    List<Issue> getIssuesByAssignee(User user);
}
