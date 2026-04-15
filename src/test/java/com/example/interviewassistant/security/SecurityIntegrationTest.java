package com.example.interviewassistant.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnUnauthorizedWhenNoToken() throws Exception {
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAccessProtectedApiWithValidToken() throws Exception {
        String loginBody = """
                {
                  "username": "admin",
                  "password": "admin123"
                }
                """;
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        String token = com.jayway.jsonpath.JsonPath.read(response, "$.accessToken");

        String questionBody = """
                {
                  "role": "Java后端工程师",
                  "seniority": "Senior",
                  "topics": ["缓存", "并发"],
                  "questionCount": 1
                }
                """;

        mockMvc.perform(post("/api/v1/interview/questions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(questionBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.generatedBy").isNotEmpty());
    }
}
