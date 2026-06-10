package com.harsha.strategy_service.infrastructure.persistence.mapper;

import com.harsha.strategy_service.domain.model.SymbolStatisticsState;
import com.harsha.strategy_service.infrastructure.persistence.entity.SymbolStatisticsEntity;
import org.springframework.stereotype.Component;

@Component
public class SymbolStatisticsMapper {

    public SymbolStatisticsState toDomain(
            SymbolStatisticsEntity entity
    ) {

        SymbolStatisticsState state =
                new SymbolStatisticsState(
                        entity.getSymbol()
                );

        state.setSampleCount(entity.getSampleCount());

        state.setMeanReturn(entity.getMeanReturn());

        state.setReturnVariance(entity.getReturnVariance());

        state.setMeanVolatility(entity.getMeanVolatility());

        state.setVolatilityVariance(entity.getVolatilityVariance());

        state.setMeanMomentumStrength(entity.getMeanMomentumStrength());

        state.setMomentumVariance(entity.getMomentumVariance());

        return state;
    }

    public SymbolStatisticsEntity toEntity(
            SymbolStatisticsState state
    ) {

        return SymbolStatisticsEntity.builder()
                .symbol(state.getSymbol())
                .sampleCount(state.getSampleCount())
                .meanReturn(state.getMeanReturn())
                .returnVariance(state.getReturnVariance())
                .meanVolatility(state.getMeanVolatility())
                .volatilityVariance(state.getVolatilityVariance())
                .meanMomentumStrength(state.getMeanMomentumStrength())
                .momentumVariance(state.getMomentumVariance())
                .build();
    }
}
