package com.sweprj.issue.domain.account;

import com.sweprj.issue.domain.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("tester")
public class Tester extends User {
    public Tester(String name, String identifier, String password) {
        super(name, identifier, password);
    }

    public Tester() {

    }

    @Override
    public String getRole() {
        return "ROLE_TESTER";
    }
}
