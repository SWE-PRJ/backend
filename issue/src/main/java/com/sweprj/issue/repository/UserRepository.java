package com.sweprj.issue.repository;

import com.sweprj.issue.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUserId(Long id);
    Optional<User> findByIdentifier(String identifier);
}
