package com.harsha.market_intelligence_service.domain.insight.model;

public record InsightRefreshDecision(
        boolean refresh,
        String reason
) {
}
