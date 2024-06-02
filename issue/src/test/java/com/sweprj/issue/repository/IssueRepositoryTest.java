package com.sweprj.issue.repository;

import com.sweprj.issue.domain.Issue;
import com.sweprj.issue.domain.Project;
import com.sweprj.issue.domain.account.Developer;
import com.sweprj.issue.domain.account.Tester;
import com.sweprj.issue.domain.enums.IssuePriority;
import com.sweprj.issue.domain.enums.IssueState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestMethodOrder(OrderAnnotation.class)
public class IssueRepositoryTest {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private Project project;
    private Tester tester;
    private Developer developer;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setName("test");
        projectRepository.save(project);

        tester = new Tester();
        tester.setIdentifier("test");
        tester.setPassword("0000");
        userRepository.save(tester);

        developer = new Developer();
        developer.setIdentifier("dev");
        developer.setPassword("0000");
        userRepository.save(developer);

        // 이제 30개의 이슈를 만들어서 저장합니다.
        for (int i = 0; i < 30; i++) {
            IssueState state;
            IssuePriority priority;

            // 이슈 상태와 우선순위를 적절히 선택합니다.
            switch (i % 5) {
                case 0:
                    state = IssueState.NEW;
                    break;
                case 1:
                    state = IssueState.ASSIGNED;
                    break;
                case 2:
                    state = IssueState.REOPENED;
                    break;
                case 3:
                    state = IssueState.CLOSED;
                    break;
                default:
                    state = IssueState.RESOLVED;
            }

            switch (i % 5) {
                case 0:
                    priority = IssuePriority.major;
                    break;
                case 1:
                    priority = IssuePriority.blocker;
                    break;
                case 2:
                    priority = IssuePriority.minor;
                    break;
                case 3:
                    priority = IssuePriority.trivial;
                    break;
                default:
                    priority = IssuePriority.critical;
            }

            Date date = new Date(123, (i/3)+1, i, 10, 15, 44);

            Issue issue = createIssue(state, priority, date);
            if(i % 2 == 0) {
                issue.setAssignee(developer);
            }

            // 이슈 생성 및 저장
            issueRepository.save(issue);
        }
    }

    @AfterEach
    public void afterEach() {
        issueRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getIssueTest() {
        List<Issue> issues = issueRepository.getIssuesByProject(project);
        assertThat(issues).hasSize(30); // 변경된 코드에 맞게 수정
    }

    @Test
    void testCountIssuesByState() {
        List<Object[]> results = issueRepository.countIssuesByState(project.getId());
        assertThat(results).hasSize(5); // 변경된 코드에 맞게 수정
    }

    @Test
    void testCountIssuesByPriority() {
        List<Object[]> results = issueRepository.countIssuesByPriority(project.getId());
        assertThat(results).hasSize(5); // 변경된 코드에 맞게 수정
    }

    @Test
    void testCountIssuesByMonth() {
        try {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-01-01 00:00:00");
            Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2024-1-31 23:59:59");
            List<Object[]> results = issueRepository.countIssuesByMonth(project.getId(), startDate, endDate);
            assertThat(results).hasSize(11);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCountIssuesByDayPerMonth() {
        try {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-01-01 00:00:00");
            Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-02-28 23:59:59");
            List<Object[]> results = issueRepository.countIssuesByDayPerMonth(project.getId(), startDate, endDate);
            assertThat(results).hasSize(3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetIssuesByAssignee() {
        List<Issue> issues = issueRepository.getIssuesByAssignee(developer);
        assertThat(issues).hasSize(15);
    }

    private Issue createIssue(IssueState state, IssuePriority priority, Date reportedAt) {
        Issue issue = new Issue();
        issue.setTitle("title");
        issue.setDescription("설명");
        issue.setReporter(tester);
        issue.setProject(project);
        issue.setState(state);
        issue.setPriority(priority);
        issue.setReportedAt(reportedAt);
        return issue;
    }
}
