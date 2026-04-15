package com.example.interviewassistant.service.ai;

import com.example.interviewassistant.dto.EvaluateAnswerRequest;
import com.example.interviewassistant.dto.EvaluateAnswerResponse;
import com.example.interviewassistant.dto.GenerateQuestionsRequest;
import com.example.interviewassistant.dto.QuestionItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LangChain4jInterviewAiClient implements InterviewAiClient {

    private static final Logger log = LoggerFactory.getLogger(LangChain4jInterviewAiClient.class);

    private final ChatLanguageModel chatLanguageModel;
    private final ObjectMapper objectMapper;
    private final InterviewAiClient fallbackClient;

    public LangChain4jInterviewAiClient(
            ChatLanguageModel chatLanguageModel,
            ObjectMapper objectMapper,
            InterviewAiClient fallbackClient
    ) {
        this.chatLanguageModel = chatLanguageModel;
        this.objectMapper = objectMapper;
        this.fallbackClient = fallbackClient;
    }

    @Override
    public List<QuestionItem> generateQuestions(GenerateQuestionsRequest request) {
        String prompt = """
                你是资深技术面试官。请严格输出 JSON 数组，不要任何额外文字。
                每个元素包含: question, intent, followUp。
                约束:
                - 岗位: %s
                - 级别: %s
                - 主题: %s
                - 数量: %d
                """.formatted(
                request.role(),
                request.seniority(),
                String.join(", ", request.topics()),
                request.questionCount()
        );

        try {
            String raw = chatLanguageModel.generate(prompt);
            JsonNode root = objectMapper.readTree(raw);
            if (!root.isArray()) {
                throw new IllegalArgumentException("Model response is not an array");
            }

            List<QuestionItem> questions = new ArrayList<>();
            for (JsonNode node : root) {
                String question = node.path("question").asText();
                String intent = node.path("intent").asText();
                String followUp = node.path("followUp").asText();
                if (!question.isBlank()) {
                    questions.add(new QuestionItem(question, intent, followUp));
                }
            }

            return questions.isEmpty() ? fallbackClient.generateQuestions(request) : questions;
        } catch (Exception ex) {
            log.warn("LangChain4j generateQuestions failed, fallback to rule-based client", ex);
            return fallbackClient.generateQuestions(request);
        }
    }

    @Override
    public EvaluateAnswerResponse evaluateAnswer(EvaluateAnswerRequest request) {
        String prompt = """
                你是技术面试评估助手。请严格输出 JSON 对象，不要任何额外文字。
                字段要求:
                - score: 0-10
                - maxScore: 固定 10
                - strengths: string[]
                - risks: string[]
                - suggestion: string

                问题: %s
                候选人回答: %s
                期待信号: %s
                """.formatted(
                request.question(),
                request.candidateAnswer(),
                String.join(", ", request.expectedSignals())
        );

        try {
            String raw = chatLanguageModel.generate(prompt);
            JsonNode root = objectMapper.readTree(raw);

            int score = Math.max(0, Math.min(10, root.path("score").asInt(6)));
            int maxScore = root.path("maxScore").asInt(10);
            List<String> strengths = readStringArray(root.path("strengths"));
            List<String> risks = readStringArray(root.path("risks"));
            String suggestion = root.path("suggestion").asText("建议补充具体指标、取舍与复盘。");

            return new EvaluateAnswerResponse(
                    score,
                    maxScore,
                    strengths.isEmpty() ? List.of("回答基本完整。") : strengths,
                    risks.isEmpty() ? List.of("未识别明显风险。") : risks,
                    suggestion,
                    providerName()
            );
        } catch (Exception ex) {
            log.warn("LangChain4j evaluateAnswer failed, fallback to rule-based client", ex);
            return fallbackClient.evaluateAnswer(request);
        }
    }

    private List<String> readStringArray(JsonNode node) {
        if (!node.isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (JsonNode item : node) {
            String value = item.asText();
            if (!value.isBlank()) {
                values.add(value);
            }
        }
        return values;
    }

    @Override
    public String providerName() {
        return "langchain4j-openai";
    }
}
