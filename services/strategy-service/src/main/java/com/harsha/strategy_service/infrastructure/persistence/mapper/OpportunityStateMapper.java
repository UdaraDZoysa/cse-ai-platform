package com.harsha.strategy_service.infrastructure.persistence.mapper;

import com.harsha.strategy_service.domain.model.OpportunityState;
import com.harsha.strategy_service.infrastructure.persistence.entity.OpportunityStateEntity;
import org.springframework.stereotype.Component;

@Component
public class OpportunityStateMapper {
    public OpportunityState toDomain(
            OpportunityStateEntity entity
    ) {
        OpportunityState state =
                new OpportunityState(
                        entity.getSymbol()
                );

        state.setConfidence(entity.getConfidence());

        state.setDirection(entity.getDirection());

        state.setStatus(entity.getStatus());

        state.setMarketRegime(entity.getMarketRegime());

        state.setLatestTrendStrength(entity.getLatestTrendStrength());

        state.setLatestMomentumStrength(entity.getLatestMomentumStrength());

        state.setLatestVolatilityStrength(entity.getLatestVolatilityStrength());

        state.setFirstDetectedAt(entity.getFirstDetectedAt());

        state.setLastUpdatedAt(entity.getLastUpdatedAt());

        state.setLastSignalAt(entity.getLastSignalAt());

        state.setPersistenceCount(entity.getPersistenceCount());

        return state;
    }

    public OpportunityStateEntity toEntity(
            OpportunityState state
    ) {

        return OpportunityStateEntity.builder()
                .symbol(state.getSymbol())
                .confidence(state.getConfidence())
                .direction(state.getDirection())
                .status(state.getStatus())
                .marketRegime(state.getMarketRegime())
                .latestTrendStrength(state.getLatestTrendStrength())
                .latestMomentumStrength(state.getLatestMomentumStrength())
                .latestVolatilityStrength(state.getLatestVolatilityStrength())
                .firstDetectedAt(state.getFirstDetectedAt())
                .lastUpdatedAt(state.getLastUpdatedAt())
                .lastSignalAt(state.getLastSignalAt())
                .persistenceCount(state.getPersistenceCount())
                .build();
    }
}
