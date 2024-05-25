package com.sweprj.issue.domain.account;

import com.sweprj.issue.domain.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("pl")
public class ProjectLeader extends User {
    public ProjectLeader(String name, String identifier, String password) {
        super(name, identifier, password);
    }

    public ProjectLeader() {

    }

    @Override
    public String getRole() {
        return "ROLE_PL";
    }
}
