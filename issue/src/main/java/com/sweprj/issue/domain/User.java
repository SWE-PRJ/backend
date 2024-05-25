package com.sweprj.issue.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String identifier;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "assignee")
    private List<IssueAssignee> assignedIssues;

//    @OneToMany(mappedBy = "commenter")
//    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<ProjectUser> projects;


    public User(String name, String identifier, String password) {
        this.name = name;
        this.identifier = identifier;
        this.password = password;
    }


    /*
        UserDetails 인터페이스의 메서드 구현
        - UserDetails: 스프링 시큐리티에서 사용자의 정보를 담는 인터페이스
     */
    @Override   // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override   // 사용자의 id 반환(고유한 값)
    public String getUsername() {
        return identifier;
    }

    @Override   // 계정 만료 여부 반환
    public boolean isAccountNonExpired() {
        return true;    // true: 만료되지 않음
    }

    @Override   // 계정 잠김 여부 반환
    public boolean isAccountNonLocked() {
        return true;    // true: 잠기지 않음
    }

    @Override   // 패스워드 만료 여부 반환
    public boolean isCredentialsNonExpired() {
        return true;   //
    }

    @Override   // 계정 활성화 여부 반환
    public boolean isEnabled() {
        return true;    // true: 활성화
    }

    public abstract String getRole();
}
