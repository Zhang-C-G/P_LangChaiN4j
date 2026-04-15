package com.example.interviewassistant.controller;

import com.example.interviewassistant.auth.AuthService;
import com.example.interviewassistant.dto.LoginRequest;
import com.example.interviewassistant.dto.LoginResponse;
import com.example.interviewassistant.exception.UnauthorizedException;
import com.example.interviewassistant.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        if (!authService.isValid(request)) {
            throw new UnauthorizedException("Authentication failed");
        }
        String token = jwtService.generateToken(request.username());
        return ResponseEntity.ok(new LoginResponse(token, "Bearer", jwtService.getExpirationSeconds()));
    }
}
