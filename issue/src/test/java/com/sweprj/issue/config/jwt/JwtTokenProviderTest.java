package com.sweprj.issue.config.jwt;

import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Test
    public void testGenerateToken() {
        // Given
        User admin = new Admin("admin", "admin_identifier", "password");

        Authentication authentication = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());

        // When
        String token = jwtTokenProvider.generateToken(authentication);

        // Then
        assertNotNull(token);
    }

    @Test
    public void testValidateToken() {
        // Given
        User admin = new Admin("admin", "admin_identifier", "password");

        Authentication authentication = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        JwtValidationType validationType = jwtTokenProvider.validateToken(token);

        // Then
        assertEquals(JwtValidationType.VALID_JWT, validationType);
    }

//    @Test
//    public void testGetUserFromJwt() {
//        // Given
//        User admin = new Admin("admin", "admin_identifier", "password");
//        admin.setUserId(1L);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());
//        String token = jwtTokenProvider.generateToken(authentication);
//
//        // When
//        Long userId = jwtTokenProvider.getUserFromJwt(token);
//
//        // Then
//        assertEquals(admin.getUserId(), userId);
//    }

    @Test
    public void testGetRoleFromJwt() {
        // Given
        User admin = new Admin("admin", "admin_identifier", "password");

        Authentication authentication = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());
        String token = jwtTokenProvider.generateToken(authentication);

        // When
        String role = jwtTokenProvider.getRoleFromJwt(token);

        // Then
        assertEquals(admin.getRole(), role);
    }
}
