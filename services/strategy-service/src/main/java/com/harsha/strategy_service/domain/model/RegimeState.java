package com.harsha.strategy_service.domain.model;

import com.harsha.contracts.events.strategy.MarketRegime;

public record RegimeState(
        MarketRegime regime,
        double confidence
) {
}
