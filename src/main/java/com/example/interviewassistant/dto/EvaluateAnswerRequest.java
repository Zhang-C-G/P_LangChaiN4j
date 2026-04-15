package com.example.interviewassistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record EvaluateAnswerRequest(
        @NotBlank(message = "question is required")
        String question,
        @NotBlank(message = "candidateAnswer is required")
        String candidateAnswer,
        @NotEmpty(message = "expectedSignals must not be empty")
        List<@NotBlank(message = "signal must not be blank") String> expectedSignals
) {
}
