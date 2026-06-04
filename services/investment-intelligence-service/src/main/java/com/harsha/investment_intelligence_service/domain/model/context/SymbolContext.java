package com.harsha.investment_intelligence_service.domain.model.context;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class SymbolContext {
    private final String symbol;

    private MarketSnapshotEvent currentMarketSnapshot;

    private final Deque<StrategyEvaluationCompletedEvent>
            strategyHistory = new ArrayDeque<>();

    private final Deque<OpportunityTransitionEvent>
            transitionHistory = new ArrayDeque<>();

    private final List<MarketInsightGeneratedEvent>
            activeInsights = new ArrayList<>();

    public SymbolContext(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public MarketSnapshotEvent currentMarketSnapshot() {
        return currentMarketSnapshot;
    }

    public void updateCurrentMarketSnapshot(MarketSnapshotEvent event) {
        this.currentMarketSnapshot = event;
    }

    public Deque<StrategyEvaluationCompletedEvent> strategyHistory() {
        return strategyHistory;
    }

    public Deque<OpportunityTransitionEvent> transitionHistory() {
        return transitionHistory;
    }

    public List<MarketInsightGeneratedEvent> activeInsights() {
        return activeInsights;
    }
}
