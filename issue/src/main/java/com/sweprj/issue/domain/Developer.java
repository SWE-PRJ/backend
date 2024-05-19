package com.sweprj.issue.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Developer")
public class Developer extends User{
}
