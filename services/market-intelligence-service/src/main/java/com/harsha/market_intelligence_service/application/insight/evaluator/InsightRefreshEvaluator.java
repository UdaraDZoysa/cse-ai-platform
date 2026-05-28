package com.harsha.market_intelligence_service.application.insight.evaluator;

import com.harsha.market_intelligence_service.domain.insight.entity.MarketInsight;
import com.harsha.market_intelligence_service.domain.insight.model.InsightRefreshDecision;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InsightRefreshEvaluator {
    public InsightRefreshDecision evaluate(
            MarketInsight insight
    ) {
        if (insight == null) {
            return new InsightRefreshDecision(
                    true,
                    "MISSING_INSIGHT"
            );
        }

        if (insight.getExpiresAt() == null) {
            return new InsightRefreshDecision(
                    true,
                    "MISSING_EXPIRATION"
            );
        }

        if (!insight.getExpiresAt()
                .isAfter(Instant.now())
        ) {
            return new InsightRefreshDecision(
                    true,
                    "EXPIRED_INSIGHT"
            );
        }

        return new InsightRefreshDecision(
                false,
                "INSIGHT_STILL_VALID"
        );
    }
}
