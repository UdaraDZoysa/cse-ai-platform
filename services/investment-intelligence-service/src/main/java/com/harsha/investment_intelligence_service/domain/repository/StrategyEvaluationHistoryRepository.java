package com.harsha.investment_intelligence_service.domain.repository;

import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;

import java.util.List;

public interface StrategyEvaluationHistoryRepository {
    void save(
            StrategyEvaluationCompletedEvent event
    );

    List<StrategyEvaluationCompletedEvent> findLatest(
            String symbol,
            int limit
    );

    List<String> findTrackedSymbols();
}
