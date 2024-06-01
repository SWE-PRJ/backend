package com.sweprj.issue.domain.account;

import com.sweprj.issue.domain.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("admin")
public class Admin extends User {
    public Admin(String identifier, String password) {
        super(identifier, password);
    }

    public Admin() {

    }

    @Override
    public String getRole() {
        return "ROLE_ADMIN";
    }
}
