package com.harsha.investment_intelligence_service.domain.model.summary;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.contracts.events.strategy.OpportunityStatus;

public record StrategySummary(
        double currentConfidence,
        double averageConfidence,
        double confidenceTrend,
        OpportunityStatus status,
        MarketRegime regime,
        int persistence
) {
}
