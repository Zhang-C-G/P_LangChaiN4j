package com.example.interviewassistant.service;

import com.example.interviewassistant.dto.EvaluateAnswerRequest;
import com.example.interviewassistant.dto.EvaluateAnswerResponse;
import com.example.interviewassistant.dto.GenerateQuestionsRequest;
import com.example.interviewassistant.dto.GenerateQuestionsResponse;

public interface InterviewService {

    GenerateQuestionsResponse generateQuestions(GenerateQuestionsRequest request);

    EvaluateAnswerResponse evaluateAnswer(EvaluateAnswerRequest request);
}
