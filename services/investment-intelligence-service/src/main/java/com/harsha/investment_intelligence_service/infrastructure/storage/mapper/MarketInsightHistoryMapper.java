package com.harsha.investment_intelligence_service.infrastructure.storage.mapper;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.MarketInsightHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketInsightHistoryMapper {
    public MarketInsightHistoryEntity toEntity(
            MarketInsightGeneratedEvent event
    ) {
        return  MarketInsightHistoryEntity.builder()
                .symbol(event.symbol())
                .occurredAt(event.occurredAt())
                .summary(event.summary())
                .reasoning(event.reasoning())
                .sentiment(event.sentiment())
                .importanceScore(event.importanceScore())
                .confidenceScore(event.confidenceScore())
                .persistenceScore(event.persistenceScore())
                .expiresAt(event.expiresAt())
                .generatedBy(event.generatedBy())
                .build();
    }

    public MarketInsightGeneratedEvent toDomain(
            MarketInsightHistoryEntity entity
    ) {
        return new MarketInsightGeneratedEvent(
                entity.getSymbol(),
                entity.getOccurredAt(),
                entity.getCompany(),
                entity.getSummary(),
                entity.getReasoning(),
                entity.getSentiment(),
                entity.getImportanceScore(),
                entity.getConfidenceScore(),
                entity.getPersistenceScore(),
                entity.getExpiresAt(),
                entity.getGeneratedBy()
        );
    }
}
