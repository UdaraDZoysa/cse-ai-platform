package com.harsha.investment_intelligence_service.application.context;

import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.repository.MarketInsightHistoryRepository;
import com.harsha.investment_intelligence_service.domain.repository.MarketSnapshotHistoryRepository;
import com.harsha.investment_intelligence_service.domain.repository.OpportunityTransitionHistoryRepository;
import com.harsha.investment_intelligence_service.domain.repository.StrategyEvaluationHistoryRepository;
import org.springframework.stereotype.Component;

@Component
public class ContextLoader {

    private final StrategyEvaluationHistoryRepository strategyRepository;
    private final OpportunityTransitionHistoryRepository transitionRepository;
    private final MarketInsightHistoryRepository insightRepository;
    private final MarketSnapshotHistoryRepository snapshotRepository;
    private final InsightRetentionPolicy insightRetentionPolicy;

    public ContextLoader(
            StrategyEvaluationHistoryRepository strategyRepository,
            OpportunityTransitionHistoryRepository transitionRepository,
            MarketInsightHistoryRepository insightRepository,
            MarketSnapshotHistoryRepository snapshotRepository,
            InsightRetentionPolicy insightRetentionPolicy
    ) {
        this.strategyRepository = strategyRepository;
        this.transitionRepository = transitionRepository;
        this.insightRepository = insightRepository;
        this.snapshotRepository = snapshotRepository;
        this.insightRetentionPolicy = insightRetentionPolicy;
    }

    public SymbolContext load(
            String symbol
    ) {
        SymbolContext context =
                new SymbolContext(symbol);

        strategyRepository
                .findLatest(symbol, 100)
                .forEach(
                        context.strategyHistory()::addLast
                );

        transitionRepository
                .findLatest(symbol, 100)
                .forEach(
                        context.transitionHistory()::addLast
                );

        insightRepository
                .findMarketInsights(symbol)
                .stream()
                .filter(insightRetentionPolicy::shouldRetain)
                .forEach(
                        context.activeInsights()::add
                );

        snapshotRepository
                .findLatest(symbol)
                .ifPresent(
                        context::updateCurrentMarketSnapshot
                );

        return context;
    }
}
