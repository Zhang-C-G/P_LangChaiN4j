package com.example.interviewassistant.controller;

import com.example.interviewassistant.dto.EvaluateAnswerRequest;
import com.example.interviewassistant.dto.EvaluateAnswerResponse;
import com.example.interviewassistant.dto.GenerateQuestionsRequest;
import com.example.interviewassistant.dto.GenerateQuestionsResponse;
import com.example.interviewassistant.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/interview")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/questions")
    public ResponseEntity<GenerateQuestionsResponse> generateQuestions(
            @Valid @RequestBody GenerateQuestionsRequest request
    ) {
        return ResponseEntity.ok(interviewService.generateQuestions(request));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateAnswerResponse> evaluateAnswer(
            @Valid @RequestBody EvaluateAnswerRequest request
    ) {
        return ResponseEntity.ok(interviewService.evaluateAnswer(request));
    }
}
