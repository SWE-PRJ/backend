package com.sweprj.issue.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Tester")
public class Tester extends User{
}
