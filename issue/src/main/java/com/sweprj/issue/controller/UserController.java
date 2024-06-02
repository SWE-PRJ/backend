package com.sweprj.issue.controller;

import com.sweprj.issue.DTO.UserLogInRequest;
import com.sweprj.issue.DTO.UserResponse;
import com.sweprj.issue.DTO.UserSignInRequest;
import com.sweprj.issue.config.jwt.JwtTokenProvider;
import com.sweprj.issue.config.jwt.JwtValidationType;
import com.sweprj.issue.config.jwt.TokenBlacklist;
import com.sweprj.issue.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklist tokenBlacklist;

    //회원가입
    @PostMapping("/admin/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserSignInRequest request, @RequestParam String role) {
        Long id = userService.register(request, role);
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

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.getTokenFromRequest(request);
        if (token != null && jwtTokenProvider.validateToken(token) == JwtValidationType.VALID_JWT) {
            tokenBlacklist.add(token);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        UserResponse user = userService.getMe();
        return ResponseEntity.ok(user);
    }

}