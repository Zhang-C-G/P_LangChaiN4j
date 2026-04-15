package com.example.interviewassistant.service.ai;

import com.example.interviewassistant.dto.EvaluateAnswerRequest;
import com.example.interviewassistant.dto.EvaluateAnswerResponse;
import com.example.interviewassistant.dto.GenerateQuestionsRequest;
import com.example.interviewassistant.dto.QuestionItem;
import java.util.List;

public interface InterviewAiClient {

    List<QuestionItem> generateQuestions(GenerateQuestionsRequest request);

    EvaluateAnswerResponse evaluateAnswer(EvaluateAnswerRequest request);

    String providerName();
}
