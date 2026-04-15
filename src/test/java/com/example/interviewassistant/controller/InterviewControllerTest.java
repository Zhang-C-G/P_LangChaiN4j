package com.example.interviewassistant.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.interviewassistant.dto.EvaluateAnswerResponse;
import com.example.interviewassistant.dto.GenerateQuestionsResponse;
import com.example.interviewassistant.dto.QuestionItem;
import com.example.interviewassistant.service.InterviewService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewService interviewService;

    @Test
    void shouldReturnValidationErrorWhenRequestInvalid() throws Exception {
        String invalidBody = """
                {
                  "role": "",
                  "seniority": "",
                  "topics": [],
                  "questionCount": 0
                }
                """;

        mockMvc.perform(post("/api/v1/interview/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldGenerateQuestionsSuccessfully() throws Exception {
        when(interviewService.generateQuestions(any()))
                .thenReturn(new GenerateQuestionsResponse(
                        List.of(new QuestionItem("Q1", "intent", "follow-up")),
                        "rule-based",
                        Instant.now()
                ));

        String body = """
                {
                  "role": "Java后端工程师",
                  "seniority": "Senior",
                  "topics": ["缓存", "并发"],
                  "questionCount": 1
                }
                """;

        mockMvc.perform(post("/api/v1/interview/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedBy").value("rule-based"))
                .andExpect(jsonPath("$.questions[0].question").value("Q1"));
    }

    @Test
    void shouldEvaluateAnswerSuccessfully() throws Exception {
        when(interviewService.evaluateAnswer(any()))
                .thenReturn(new EvaluateAnswerResponse(
                        8,
                        10,
                        List.of("结构清晰"),
                        List.of("缺少量化指标"),
                        "补充指标",
                        "rule-based"
                ));

        String body = """
                {
                  "question": "如何提升接口性能？",
                  "candidateAnswer": "我会先做性能分析，再缓存热点数据。",
                  "expectedSignals": ["性能分析", "缓存", "监控"]
                }
                """;

        mockMvc.perform(post("/api/v1/interview/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(8))
                .andExpect(jsonPath("$.evaluatedBy").value("rule-based"));
    }
}
