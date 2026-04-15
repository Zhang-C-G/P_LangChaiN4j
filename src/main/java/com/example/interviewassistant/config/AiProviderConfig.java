package com.example.interviewassistant.config;

import com.example.interviewassistant.service.ai.InterviewAiClient;
import com.example.interviewassistant.service.ai.LangChain4jInterviewAiClient;
import com.example.interviewassistant.service.ai.RuleBasedInterviewAiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiProviderConfig {

    @Bean
    public InterviewAiClient interviewAiClient(
            @Value("${app.ai.provider:rule}") String provider,
            @Value("${app.ai.openai.api-key:}") String openAiApiKey,
            @Value("${app.ai.openai.model:gpt-4o-mini}") String modelName,
            @Value("${app.ai.openai.temperature:0.3}") double temperature,
            ObjectMapper objectMapper,
            RuleBasedInterviewAiClient ruleBasedClient
    ) {
        if ("openai".equalsIgnoreCase(provider) && !openAiApiKey.isBlank()) {
            ChatLanguageModel model = OpenAiChatModel.builder()
                    .apiKey(openAiApiKey)
                    .modelName(modelName)
                    .temperature(temperature)
                    .build();
            return new LangChain4jInterviewAiClient(model, objectMapper, ruleBasedClient);
        }
        return ruleBasedClient;
    }
}
