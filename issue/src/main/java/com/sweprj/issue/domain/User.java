package com.sweprj.issue.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String identifier;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "assignee")
    private List<IssueAssignee> assignedIssues;

    @OneToMany(mappedBy = "commenter")
    private List<Comment> comments;
}
