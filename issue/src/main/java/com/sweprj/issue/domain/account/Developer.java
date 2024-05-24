package com.sweprj.issue.domain.account;

import com.sweprj.issue.domain.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("dev")
public class Developer extends User {
    public Developer(String name, String identifier, String password) {
        super(name, identifier, password);
    }

    @Override
    public String getRole() {
        return "ROLE_DEV";
    }
}
