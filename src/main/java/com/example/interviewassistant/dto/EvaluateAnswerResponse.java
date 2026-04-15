package com.example.interviewassistant.dto;

import java.util.List;

public record EvaluateAnswerResponse(
        int score,
        int maxScore,
        List<String> strengths,
        List<String> risks,
        String suggestion,
        String evaluatedBy
) {
}
