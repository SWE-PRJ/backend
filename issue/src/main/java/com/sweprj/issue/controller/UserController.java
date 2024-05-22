package com.sweprj.issue.controller;

import com.sweprj.issue.dto.UserLogInRequest;
import com.sweprj.issue.dto.UserSignInRequest;
import com.sweprj.issue.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    //생성자 주입
    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody UserSignInRequest request) {
        Long id = userService.signup(request);
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        return ResponseEntity.created(null).body(response);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLogInRequest request) {
        Map<String, Object> response = userService.login(request);
        return ResponseEntity.ok(response);
    }

}