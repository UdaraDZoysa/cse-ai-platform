package com.harsha.investment_intelligence_service.domain.repository;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;

import java.util.List;

public interface MarketInsightHistoryRepository {
    void save(MarketInsightGeneratedEvent event);

    List<MarketInsightGeneratedEvent> findMarketInsights(String symbol);
}
