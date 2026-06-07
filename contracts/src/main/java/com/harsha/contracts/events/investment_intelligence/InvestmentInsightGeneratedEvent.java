package com.harsha.contracts.events.investment_intelligence;

import com.harsha.contracts.events.DomainEvent;
import com.harsha.contracts.events.investment_intelligence.enums.*;
import com.harsha.contracts.messaging.EventType;

import java.util.List;

public record InvestmentInsightGeneratedEvent(
        String symbol,
        long occurredAt,

        String executiveSummary,

        MarketSentiment sentiment,
        String marketReasoning,

        RecommendedAction action,
        String actionReasoning,

        TimeHorizon timeHorizon,

        ExpectedDirection expectedDirection,
        ExpectedMagnitude expectedMagnitude,
        String marketBehaviorJustification,

        List<String> supportingFactors,
        List<String> risks,
        List<String> contextLimitations,
        List<String> invalidationConditions,

        RiskLevel riskLevel,
        String riskJustification,

        int confidenceScore,
        String confidenceReasoning,

        String generatedBy,
        String model

) implements DomainEvent {
    @Override
    public EventType eventType() {
        return EventType.INVESTMENT_INSIGHT_GENERATED_EVENT;
    }
}
