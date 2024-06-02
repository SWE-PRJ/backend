package com.sweprj.issue.config.jwt;

import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Tester;
import com.sweprj.issue.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    private User testUser;
    private Authentication authentication;

    @Before
    public void setup() {
        String identifier = "testUser";
        String password = "testPassword";

        // 이미 존재하는 사용자인지 확인
        Optional<User> existingUser = userRepository.findByIdentifier(identifier);

        if (existingUser.isPresent()) {
            // 이미 존재하는 사용자가 있다면 해당 사용자를 테스트 사용자로 설정
            testUser = (Tester) existingUser.get();
        } else {
            // 존재하지 않는 경우, 새로운 테스트 사용자 생성

            testUser = new Tester(identifier, passwordEncoder.encode(password));
            userRepository.save(testUser);
        }

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(identifier, password);

        authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    @Test
    public void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(authentication);
        assertNotNull(token);
    }

    @Test
    public void testValidateToken() {
        String token = jwtTokenProvider.generateToken(authentication);

        JwtValidationType validationType = jwtTokenProvider.validateToken(token);
        assertEquals(JwtValidationType.VALID_JWT, validationType);
    }

    @Test
    public void testValidateExpiredToken() {
        // Create an expired token for testing (manually create one with past expiration date)
        String expiredToken = Jwts.builder()
                .setClaims(Jwts.claims().setSubject("expiredTestUser").setExpiration(new Date(System.currentTimeMillis() - 1000)))
                .signWith(jwtTokenProvider.getSigningKey())
                .compact();

        JwtValidationType validationType = jwtTokenProvider.validateToken(expiredToken);
        assertEquals(JwtValidationType.EXPIRED_JWT_TOKEN, validationType);
    }

    @Test
    public void testGetUserFromJwt() {
        String token = jwtTokenProvider.generateToken(authentication);
        Long userId = jwtTokenProvider.getUserFromJwt(token);

        assertEquals(testUser.getUserId(), userId);
    }

    @Test
    public void testGetRoleFromJwt() {
        String token = jwtTokenProvider.generateToken(authentication);
        String role = jwtTokenProvider.getRoleFromJwt(token);

        assertEquals("ROLE_TESTER", role);
    }
}
