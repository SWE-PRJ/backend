package com.sweprj.issue.domain;

import com.sweprj.issue.domain.enums.IssueState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class Issue {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "fixer_id")
    private User fixer;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;


    private String priority;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'NEW'")
    private IssueState state = IssueState.NEW;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private Date reportedAt;

    @OneToMany(mappedBy = "issue")
    private List<Comment> comments;
}
