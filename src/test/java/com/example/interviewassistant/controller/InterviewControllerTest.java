package com.example.interviewassistant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = InterviewController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.example.interviewassistant.security.JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
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
                  "role": "Java Backend Engineer",
                  "seniority": "Senior",
                  "topics": ["cache", "concurrency"],
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
                        List.of("clear structure"),
                        List.of("missing metrics"),
                        "add measurable metrics",
                        "rule-based"
                ));

        String body = """
                {
                  "question": "How do you improve API performance?",
                  "candidateAnswer": "I first profile, then cache hot keys, and monitor latency.",
                  "expectedSignals": ["profiling", "cache", "monitoring"]
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
