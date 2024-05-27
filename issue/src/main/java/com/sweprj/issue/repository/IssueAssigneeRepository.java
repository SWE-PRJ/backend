package com.sweprj.issue.repository;

import com.sweprj.issue.domain.account.Developer;
import com.sweprj.issue.domain.IssueAssignee;
import com.sweprj.issue.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueAssigneeRepository extends JpaRepository<IssueAssignee, Long> {

    List<IssueAssignee> getIssueAssigneesByAssignee(User user);
}
