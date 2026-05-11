package com.harsha.analysis_service.service.feature.model;

import com.harsha.events.market.StockTickEvent;

import java.util.Deque;

public record FeatureContext(
        String symbol,
        Deque<StockTickEvent> window
) {
}
