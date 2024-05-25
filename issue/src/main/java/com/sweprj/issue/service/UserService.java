package com.sweprj.issue.service;

import com.sweprj.issue.domain.User;
import com.sweprj.issue.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User findById(Long id) {
        return userRepository.findUserByUserId(id);
    }

}
