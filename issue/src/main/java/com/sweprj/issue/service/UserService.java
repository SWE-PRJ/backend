package com.sweprj.issue.service;

import com.sweprj.issue.domain.User;
import com.sweprj.issue.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    public Long signup(UserDTO userDTO) {
        // 1. dto -> entity 변환
        // 2. repository의 save 메서드 호출
        User memberEntity = userDTO.toEntity(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(memberEntity).getId();
        // repository의 save 메서드 호출 (조건. entity 객체를 넘겨줘야 함)
    }
}
