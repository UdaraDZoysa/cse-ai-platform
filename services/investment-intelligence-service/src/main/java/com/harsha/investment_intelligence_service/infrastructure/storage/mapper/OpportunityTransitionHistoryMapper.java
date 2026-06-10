package com.harsha.investment_intelligence_service.infrastructure.storage.mapper;

import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.OpportunityTransitionHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class OpportunityTransitionHistoryMapper {
    public OpportunityTransitionHistoryEntity toEntity(
            OpportunityTransitionEvent event
    ) {
        return OpportunityTransitionHistoryEntity.builder()
                .symbol(event.symbol())
                .occurredAt(event.occurredAt())
                .previousStatus(event.previousStatus())
                .currentStatus(event.currentStatus())
                .previousConfidence(event.previousConfidence())
                .currentConfidence(event.currentConfidence())
                .previousDirection(event.previousDirection())
                .currentDirection(event.currentDirection())
                .previousRegime(event.previousRegime())
                .currentRegime(event.currentRegime())
                .persistenceCount(event.persistenceCount())
                .reasons(event.reasons())
                .build();
    }

    public OpportunityTransitionEvent toDomain(
            OpportunityTransitionHistoryEntity entity
    ) {
        return new OpportunityTransitionEvent(
                entity.getSymbol(),
                entity.getOccurredAt(),
                entity.getPreviousStatus(),
                entity.getCurrentStatus(),
                entity.getPreviousConfidence(),
                entity.getCurrentConfidence(),
                entity.getPreviousDirection(),
                entity.getCurrentDirection(),
                entity.getPreviousRegime(),
                entity.getCurrentRegime(),
                entity.getPersistenceCount(),
                entity.getReasons()
        );
    }
}
