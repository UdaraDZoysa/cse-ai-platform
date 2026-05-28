package com.harsha.market_intelligence_service.narrative.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exa")
public record ExaProperties(
        String apiKey,
        String baseUrl
) {
}