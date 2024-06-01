package com.sweprj.issue.service;

import com.sweprj.issue.DTO.UserLogInRequest;
import com.sweprj.issue.DTO.UserSignInRequest;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Test
    public void testRegister() {
        UserSignInRequest request = new UserSignInRequest();
        request.setIdentifier("test_identifier");
        request.setPassword("password");

        User admin = new Admin("admin_identifier", passwordEncoder.encode("admin_password"));
        userRepository.save(admin);

        Long userId = userService.register(request,  "admin_identifier");

        assertNotNull(userId);
        User savedUser = userRepository.findById(userId).orElse(null);
        assertNotNull(savedUser);
        assertEquals("ROLE_ADMIN", savedUser.getRole());
    }

    @Test
    public void testLogin() {
        UserSignInRequest signUpRequest = new UserSignInRequest();
        signUpRequest.setIdentifier("user_identifier");
        signUpRequest.setPassword("password");

        userService.signup(signUpRequest, "admin");

        UserLogInRequest loginRequest = new UserLogInRequest();
        loginRequest.setIdentifier("user_identifier");
        loginRequest.setPassword("password");

        Map<String, Object> loginResponse = userService.login(loginRequest);

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.get("token"));
        assertEquals("ROLE_ADMIN", loginResponse.get("role"));
    }

    @Test
    public void testLoadUserByUsername() {
        User user = new Admin("user_identifier", passwordEncoder.encode("password"));
        userRepository.save(user);

        UserDetails userDetails = userService.loadUserByUsername("user_identifier");

        assertNotNull(userDetails);
        assertEquals("user_identifier", userDetails.getUsername());
    }
}
