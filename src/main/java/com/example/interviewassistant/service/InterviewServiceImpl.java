package com.example.interviewassistant.service;

import com.example.interviewassistant.dto.EvaluateAnswerRequest;
import com.example.interviewassistant.dto.EvaluateAnswerResponse;
import com.example.interviewassistant.dto.GenerateQuestionsRequest;
import com.example.interviewassistant.dto.GenerateQuestionsResponse;
import com.example.interviewassistant.persistence.InterviewActionType;
import com.example.interviewassistant.persistence.InterviewRecordService;
import com.example.interviewassistant.service.ai.InterviewAiClient;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewAiClient interviewAiClient;
    private final InterviewRecordService recordService;

    public InterviewServiceImpl(InterviewAiClient interviewAiClient, InterviewRecordService recordService) {
        this.interviewAiClient = interviewAiClient;
        this.recordService = recordService;
    }

    @Override
    public GenerateQuestionsResponse generateQuestions(GenerateQuestionsRequest request) {
        GenerateQuestionsResponse response = new GenerateQuestionsResponse(
                interviewAiClient.generateQuestions(request),
                interviewAiClient.providerName(),
                Instant.now()
        );
        recordService.save(InterviewActionType.QUESTION_GENERATION, response.generatedBy(), request, response);
        return response;
    }

    @Override
    public EvaluateAnswerResponse evaluateAnswer(EvaluateAnswerRequest request) {
        EvaluateAnswerResponse response = interviewAiClient.evaluateAnswer(request);
        recordService.save(InterviewActionType.ANSWER_EVALUATION, response.evaluatedBy(), request, response);
        return response;
    }
}
