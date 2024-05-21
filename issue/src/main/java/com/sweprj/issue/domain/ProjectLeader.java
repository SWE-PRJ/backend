package com.sweprj.issue.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PL")
public class ProjectLeader extends User{
}
