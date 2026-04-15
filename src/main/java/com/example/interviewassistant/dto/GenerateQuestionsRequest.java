package com.example.interviewassistant.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record GenerateQuestionsRequest(
        @NotBlank(message = "role is required")
        String role,
        @NotBlank(message = "seniority is required")
        String seniority,
        @NotEmpty(message = "topics must not be empty")
        List<@NotBlank(message = "topic must not be blank") String> topics,
        @Min(value = 1, message = "questionCount must be >= 1")
        @Max(value = 8, message = "questionCount must be <= 8")
        int questionCount
) {
}
