package com.example.interviewassistant.dto;

import java.time.Instant;
import java.util.List;

public record GenerateQuestionsResponse(
        List<QuestionItem> questions,
        String generatedBy,
        Instant generatedAt
) {
}
