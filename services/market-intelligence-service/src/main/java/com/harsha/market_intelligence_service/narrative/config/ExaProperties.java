package com.harsha.market_intelligence_service.narrative.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.stringtemplate.v4.ST;

@ConfigurationProperties(prefix = "exa")
public record ExaProperties(
        String apiKey,
        String baseUrl
) {
}
