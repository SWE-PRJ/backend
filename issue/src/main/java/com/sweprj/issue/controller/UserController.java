package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.UserLogInRequest;
import com.sweprj.issue.DTO.UserResponse;
import com.sweprj.issue.DTO.UserSignInRequest;
import com.sweprj.issue.domain.User;
import com.sweprj.issue.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    //생성자 주입
    private final UserService userService;

    //회원가입
    @PostMapping("/admin/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserSignInRequest request, @RequestParam String role, @RequestParam String adminIdentifier) {
        Long id = userService.register(request, role, adminIdentifier);
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        return ResponseEntity.created(null).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody UserSignInRequest request, @RequestParam String role) {
        Long id = userService.signup(request, role);
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

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

}