package com.harsha.market_intelligence_service.application.insight.mapper;

import com.harsha.market_intelligence_service.domain.insight.entity.MarketInsight;
import com.harsha.market_intelligence_service.domain.insight.model.InsightGenerationContext;
import com.harsha.market_intelligence_service.domain.insight.model.MarketInsightResult;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MarketInsightMapper {
    public MarketInsight map(
            InsightGenerationContext context,
            MarketInsightResult result
    ) {
        double persistence =
                normalizeScore(
                        result.persistenceScore()
                );

        return MarketInsight.builder()
                .symbol(context.symbol())
                .company(context.company())
                .summary(result.summary())
                .reasoning(result.reasoning())
                .sentiment(result.sentiment())
                .importanceScore(
                        normalizeScore(
                                result.importanceScore()
                        )
                )
                .persistenceScore(persistence)
                .confidenceScore(
                        normalizeScore(
                                result.confidenceScore()
                        )
                )
                .generatedAt(Instant.now())
                .expiresAt(
                        Instant.now().plusSeconds(
                                (long) (86400 * Math.max(persistence, 0.25))
                        )
                )
                .generatedBy("groq")
                .build();
    }
    private double normalizeScore(
            double value
    ) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
