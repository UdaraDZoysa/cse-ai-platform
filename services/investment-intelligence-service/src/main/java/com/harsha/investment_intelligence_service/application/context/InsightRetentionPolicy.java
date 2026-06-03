package com.harsha.investment_intelligence_service.application.context;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.investment_intelligence_service.util.InsightScoring;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class InsightRetentionPolicy {
    public boolean shouldRetain(
            MarketInsightGeneratedEvent insight
    ) {
        Duration retention = determineRetention(insight);

        long ageMillis =
                System.currentTimeMillis() - insight.occurredAt();

        return ageMillis <= retention.toMillis();
    }

    private Duration determineRetention(
            MarketInsightGeneratedEvent insight
    ) {
        double score = InsightScoring.score(insight);

        if (score >= 0.90) {
            return Duration.ofDays(14);
        }

        if (score >= 0.75) {
            return Duration.ofDays(7);
        }

        if (score >= 0.60) {
            return Duration.ofDays(3);
        }

        return Duration.ofDays(1);
    }
}
