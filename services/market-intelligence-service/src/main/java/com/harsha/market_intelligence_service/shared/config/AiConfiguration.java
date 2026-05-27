package com.harsha.market_intelligence_service.shared.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfiguration {
    @Bean
    public ChatClient.Builder chatClientBuilder(
            OpenAiChatModel chatModel
    ) {

        return ChatClient.builder(chatModel);
    }
}
