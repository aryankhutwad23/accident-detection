package com.accidentdetection.accident.detection.backend.controller;

import com.accidentdetection.accident.detection.backend.dto.AuthResponse;
import com.accidentdetection.accident.detection.backend.dto.FirstRegistration;
import com.accidentdetection.accident.detection.backend.dto.LoginRequest;
import com.accidentdetection.accident.detection.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** REGISTER USER (name, email, password) **/
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody FirstRegistration request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /** LOGIN USER (email + password) **/
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}