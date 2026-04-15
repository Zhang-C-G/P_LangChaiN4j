package com.example.interviewassistant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.interviewassistant.dto.EvaluateAnswerRequest;
import com.example.interviewassistant.dto.GenerateQuestionsRequest;
import com.example.interviewassistant.service.ai.RuleBasedInterviewAiClient;
import java.util.List;
import org.junit.jupiter.api.Test;

class RuleBasedInterviewAiClientTest {

    private final RuleBasedInterviewAiClient client = new RuleBasedInterviewAiClient();

    @Test
    void shouldGenerateExpectedQuestionCount() {
        GenerateQuestionsRequest request = new GenerateQuestionsRequest(
                "Java后端工程师",
                "Senior",
                List.of("缓存", "并发"),
                3
        );

        var questions = client.generateQuestions(request);
        assertEquals(3, questions.size());
    }

    @Test
    void shouldScoreHigherWhenSignalsMatch() {
        EvaluateAnswerRequest request = new EvaluateAnswerRequest(
                "你如何优化接口性能？",
                "我会先做压测，然后加缓存，最后做监控与告警。",
                List.of("压测", "缓存", "监控")
        );

        var result = client.evaluateAnswer(request);
        assertTrue(result.score() >= 8);
        assertFalse(result.strengths().isEmpty());
    }
}
