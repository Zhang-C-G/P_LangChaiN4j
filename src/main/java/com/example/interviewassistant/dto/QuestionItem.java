package com.example.interviewassistant.dto;

public record QuestionItem(
        String question,
        String intent,
        String followUp
) {
}
