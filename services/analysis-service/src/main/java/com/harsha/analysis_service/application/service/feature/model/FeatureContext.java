package com.harsha.analysis_service.application.service.feature.model;

import com.harsha.contracts.events.market.StockTickEvent;

import java.util.Deque;

public record FeatureContext(
        String symbol,
        Deque<StockTickEvent> window
) {
}
