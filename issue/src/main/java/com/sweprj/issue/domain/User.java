package com.sweprj.issue.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String identifier;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "assignee")
    private List<IssueAssignee> assignedIssues;

//    @OneToMany(mappedBy = "commenter")
//    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<ProjectUser> projects;

    @Builder
    public User(String name, String identifier, String password) {
        this.name = name;
        this.identifier = identifier;
        this.password = password;
    }
}
