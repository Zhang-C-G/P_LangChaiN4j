package com.example.interviewassistant.service.ai;

import com.example.interviewassistant.dto.EvaluateAnswerRequest;
import com.example.interviewassistant.dto.EvaluateAnswerResponse;
import com.example.interviewassistant.dto.GenerateQuestionsRequest;
import com.example.interviewassistant.dto.QuestionItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedInterviewAiClient implements InterviewAiClient {

    @Override
    public List<QuestionItem> generateQuestions(GenerateQuestionsRequest request) {
        List<String> topics = request.topics();
        return IntStream.range(0, request.questionCount())
                .mapToObj(i -> {
                    String topic = topics.get(i % topics.size());
                    String question = String.format("你如何在 %s 场景里落地 %s，并衡量效果？", request.role(), topic);
                    String intent = String.format("考察候选人对 %s 的工程化理解与业务权衡能力", topic);
                    String followUp = String.format("如果你是 %s 级别，你会如何处理上线后的异常与回滚？", request.seniority());
                    return new QuestionItem(question, intent, followUp);
                })
                .toList();
    }

    @Override
    public EvaluateAnswerResponse evaluateAnswer(EvaluateAnswerRequest request) {
        String normalizedAnswer = request.candidateAnswer().toLowerCase(Locale.ROOT);

        List<String> matchedSignals = new ArrayList<>();
        List<String> missingSignals = new ArrayList<>();

        for (String signal : request.expectedSignals()) {
            if (normalizedAnswer.contains(signal.toLowerCase(Locale.ROOT))) {
                matchedSignals.add("命中关键点: " + signal);
            } else {
                missingSignals.add("缺少关键点: " + signal);
            }
        }

        int maxScore = 10;
        int score = request.expectedSignals().isEmpty()
                ? 6
                : (int) Math.round((matchedSignals.size() * 1.0 / request.expectedSignals().size()) * maxScore);

        if (score < 4) {
            score = 4;
        }

        String suggestion = missingSignals.isEmpty()
                ? "回答结构完整。建议补充可量化指标与一次真实故障复盘，让说服力更强。"
                : "建议按 STAR 结构回答，并补充以下缺失关键点: " + String.join("；", missingSignals);

        return new EvaluateAnswerResponse(
                score,
                maxScore,
                matchedSignals.isEmpty() ? List.of("回答有基本思路，表达清晰。") : matchedSignals,
                missingSignals.isEmpty() ? List.of("未发现明显风险。") : missingSignals,
                suggestion,
                providerName()
        );
    }

    @Override
    public String providerName() {
        return "rule-based";
    }
}
