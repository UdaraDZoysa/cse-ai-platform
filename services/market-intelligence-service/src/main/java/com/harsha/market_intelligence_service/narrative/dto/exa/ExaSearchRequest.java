package com.harsha.market_intelligence_service.narrative.dto.exa;

import java.util.Map;

public record ExaSearchRequest(
        String query,
        String type,
        int numResults,
        String systemPrompt,
        Map<String, Object> outputSchema,
        Map<String, Object> contents
) {
}
