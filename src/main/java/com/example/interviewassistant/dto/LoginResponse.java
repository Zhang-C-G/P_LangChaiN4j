package com.example.interviewassistant.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {
}
