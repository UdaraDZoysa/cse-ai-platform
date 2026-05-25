package com.harsha.market_intelligence_service.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient cseWebClient(
            @Value("${cse-base-url}") String sceBaseUrl
    ) {
        return WebClient.builder()
                .baseUrl(sceBaseUrl)
                .build();
    }
}
