package com.example.interviewassistant.service;

import com.example.interviewassistant.dto.EvaluateAnswerRequest;
import com.example.interviewassistant.dto.EvaluateAnswerResponse;
import com.example.interviewassistant.dto.GenerateQuestionsRequest;
import com.example.interviewassistant.dto.GenerateQuestionsResponse;
import com.example.interviewassistant.service.ai.InterviewAiClient;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewAiClient interviewAiClient;

    public InterviewServiceImpl(InterviewAiClient interviewAiClient) {
        this.interviewAiClient = interviewAiClient;
    }

    @Override
    public GenerateQuestionsResponse generateQuestions(GenerateQuestionsRequest request) {
        return new GenerateQuestionsResponse(
                interviewAiClient.generateQuestions(request),
                interviewAiClient.providerName(),
                Instant.now()
        );
    }

    @Override
    public EvaluateAnswerResponse evaluateAnswer(EvaluateAnswerRequest request) {
        return interviewAiClient.evaluateAnswer(request);
    }
}
