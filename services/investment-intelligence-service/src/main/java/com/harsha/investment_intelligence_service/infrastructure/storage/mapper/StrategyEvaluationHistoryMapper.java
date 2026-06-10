package com.harsha.investment_intelligence_service.infrastructure.storage.mapper;

import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.StrategyEvaluationHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class StrategyEvaluationHistoryMapper {

    public StrategyEvaluationHistoryEntity toEntity(
            StrategyEvaluationCompletedEvent event
    ) {

        return StrategyEvaluationHistoryEntity.builder()
                .symbol(event.symbol())
                .occurredAt(event.occurredAt())
                .marketRegime(event.marketRegime())
                .regimeConfidence(event.regimeConfidence())
                .confidence(event.confidence())
                .direction(event.direction())
                .status(event.status())
                .persistence(event.persistence())
                .statisticalReady(event.statisticalReady())
                .sampleCount(event.sampleCount())
                .build();
    }

    public StrategyEvaluationCompletedEvent toDomain(
            StrategyEvaluationHistoryEntity entity
    ) {

        return new StrategyEvaluationCompletedEvent(
                entity.getSymbol(),
                entity.getOccurredAt(),
                entity.getMarketRegime(),
                entity.getRegimeConfidence(),
                entity.getConfidence(),
                entity.getDirection(),
                entity.getStatus(),
                entity.getPersistence(),
                entity.isStatisticalReady(),
                entity.getSampleCount()
        );
    }
}
