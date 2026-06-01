package com.harsha.strategy_service.domain.model;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.contracts.events.strategy.OpportunityStatus;
import com.harsha.contracts.events.strategy.SignalDirection;

public record OpportunitySnapshot(
        double confidence,
        SignalDirection direction,
        OpportunityStatus status,
        MarketRegime marketRegime
) {
    public static OpportunitySnapshot from(
            OpportunityState state
    ) {
        return new OpportunitySnapshot(
                state.getConfidence(),
                state.getDirection(),
                state.getStatus(),
                state.getMarketRegime()
        );
    }
}
