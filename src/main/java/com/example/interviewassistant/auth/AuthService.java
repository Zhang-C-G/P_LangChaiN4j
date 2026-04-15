package com.example.interviewassistant.auth;

import com.example.interviewassistant.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final String configuredUsername;
    private final String configuredPassword;

    public AuthService(
            @Value("${app.security.demo-user.username:admin}") String configuredUsername,
            @Value("${app.security.demo-user.password:admin123}") String configuredPassword
    ) {
        this.configuredUsername = configuredUsername;
        this.configuredPassword = configuredPassword;
    }

    public boolean isValid(LoginRequest request) {
        return configuredUsername.equals(request.username()) && configuredPassword.equals(request.password());
    }
}
