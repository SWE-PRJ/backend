package com.sweprj.issue.domain;

import com.sweprj.issue.domain.enums.IssueState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private String priority;

    @Enumerated(EnumType.STRING)
    private IssueState state;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private Date reportedAt;

}
