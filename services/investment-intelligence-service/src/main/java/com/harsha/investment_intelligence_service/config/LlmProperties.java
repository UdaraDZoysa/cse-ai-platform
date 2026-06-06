package com.harsha.investment_intelligence_service.config;

import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ProviderType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "llm")
public record LlmProperties(
        ProviderType providerType,
        String model,
        String apiKey,
        String baseUrl
) {
}
