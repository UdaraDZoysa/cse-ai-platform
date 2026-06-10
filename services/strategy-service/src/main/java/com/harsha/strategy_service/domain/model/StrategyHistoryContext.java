package com.harsha.strategy_service.domain.model;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.contracts.events.strategy.OpportunityStatus;

import java.util.List;

public record StrategyHistoryContext(
        List<Double> confidenceHistory,
        List<OpportunityStatus> statusHistory,
        List<MarketRegime> regimeHistory
) {
}
