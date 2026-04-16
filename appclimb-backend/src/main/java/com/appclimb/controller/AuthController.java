package com.appclimb.controller;

import com.appclimb.dto.request.GymApplyRequest;
import com.appclimb.dto.request.LoginRequest;
import com.appclimb.dto.request.RegisterRequest;
import com.appclimb.dto.response.AuthResponse;
import com.appclimb.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * 지점 가입 신청 (계정 생성 + 지점 신청 동시 처리, 비로그인 상태로 접근 가능)
     */
    @PostMapping("/apply")
    public ResponseEntity<Void> applyWithGym(@Valid @RequestBody GymApplyRequest request) {
        authService.applyWithGym(request);
        return ResponseEntity.ok().build();
    }
}
