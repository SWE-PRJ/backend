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

    // 특정 프로젝트에 속한 이슈 모음
    List<Issue> getIssuesByProject(Project project);

    @Query("SELECT COUNT(i) " +
            "FROM Issue i " +
            "WHERE i.project.id = :projectId AND i.reportedAt BETWEEN :startDate AND :endDate")
    Long countAllIssuesWithinDateRange(Long projectId, Date startDate, Date endDate);

    // project에서 각 state별 이슈의 개수
    @Query("SELECT i.state, COUNT(i) " +
            "FROM Issue i " +
            "WHERE i.project.id = :projectId AND i.reportedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY i.state " +
            "ORDER BY i.state")
    List<Object[]> countIssuesByState(Long projectId, Date startDate, Date endDate);

    // project에서 각 priority별 이슈의 개수
    @Query("SELECT i.priority, COUNT(i) " +
            "FROM Issue i " +
            "WHERE i.project.id = :projectId AND i.reportedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY i.priority")
    List<Object[]> countIssuesByPriority(Long projectId, Date startDate, Date endDate);

    // 월별 이슈량
    @Query("SELECT DATE_FORMAT(i.reportedAt, '%Y-%m'), COUNT(i) " +
            "FROM Issue i WHERE i.project.id = :projectId AND i.reportedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_FORMAT(i.reportedAt, '%Y-%m') "+
            "ORDER BY DATE_FORMAT(i.reportedAt, '%Y-%m')")
    List<Object[]> countIssuesByMonth(Long projectId, Date startDate, Date endDate);

    // 일별 이슈량
    @Query("SELECT DATE_FORMAT(i.reportedAt, '%Y-%m'), DAY(i.reportedAt), COUNT(i) " +
            "FROM Issue i WHERE i.project.id = :projectId AND i.reportedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_FORMAT(i.reportedAt, '%Y-%m'), DAY(i.reportedAt) "+
            "ORDER BY DATE_FORMAT(i.reportedAt, '%Y-%m'), DAY(i.reportedAt)")
    List<Object[]> countIssuesByDayPerMonth(Long projectId, Date startDate, Date endDate);

    // 특정 프로젝트에 속하고 특정 개발자에게 할당된 이슈들 서칭
    List<Issue> getIssuesByProjectAndAssignee(Project project, User user);

    // 특정 프로젝트에 속하고 특정 테스터가 제안한 이슈들 서칭
    List<Issue> getIssuesByProjectAndReporter(Project project, User user);
}
