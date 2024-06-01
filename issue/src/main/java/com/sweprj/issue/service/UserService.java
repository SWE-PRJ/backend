package com.sweprj.issue.service;

import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.domain.account.Admin;
import com.sweprj.issue.domain.account.Developer;
import com.sweprj.issue.domain.account.ProjectLeader;
import com.sweprj.issue.domain.account.Tester;
import com.sweprj.issue.DTO.UserLogInRequest;
import com.sweprj.issue.DTO.UserSignInRequest;
import com.sweprj.issue.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    //admin 전용 회원가입
    @Transactional
    public Long register(UserSignInRequest dto, String role, String adminIdentifier) {
        User adminUser = userRepository.findByIdentifier(adminIdentifier)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));

        if (!adminUser.getRole().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admin can register users");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = createUserByRole(dto.getIdentifier(), encodedPassword, role);

        return userRepository.save(user).getUserId();
    }

    private User createUserByRole(String identifier, String password, String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return new Admin(identifier, password);
            case "pl":
                return new ProjectLeader(identifier, password);
            case "dev":
                return new Developer(identifier, password);
            case "tester":
                return new Tester(identifier, password);
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Transactional
    public Long signup(UserSignInRequest dto, String role) {
        // 1. dto -> entity 변환
        // 2. repository의 save 메서드 호출
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = new Admin(dto.getIdentifier(), encodedPassword);;


//        User userEntity = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user).getUserId();
        // repository의 save 메서드 호출 (조건. entity 객체를 넘겨줘야 함)
    }

    /**
     * 로그인
     */
    @Transactional
    public Map<String, Object> login(UserLogInRequest dto) {

        Optional<User> optionalUser = userRepository.findByIdentifier(dto.getIdentifier());

        // name이 일치하는 Member가 없는 경우
        if (optionalUser.isEmpty()) {
            throw new AuthenticationException("아이디 또는 비밀번호가 존재하지 않습니다.") {};
        }

        User user = optionalUser.get();

        // password가 일치하지 않으면 null 반환
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("아이디 또는 비밀번호가 일치하지 않습니다.") {};
        }


        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getIdentifier(), dto.getPassword());


        // authenticationToken 객체를 통해 Authentication 객체 생성
        // 이 과정에서 CustomUserDetailsService 에서 우리가 재정의한 loadUserByUsername 메서드 호출
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("id", user.getUserId());
        body.put("role", user.getRole());
        return body;
    }

    /**
     * ID로 회원 조회
     */
  
    public User findById(Long id) {
        return userRepository.findUserByUserId(id);
    }


    /**
     * 닉네임으로 회원 조회
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        System.out.println("loadUserByUsername 유저 찾음: " + user);
        return user;
    }
}
