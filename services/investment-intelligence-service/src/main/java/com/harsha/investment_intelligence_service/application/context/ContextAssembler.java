package com.harsha.investment_intelligence_service.application.context;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AIReasoningContext;
import com.harsha.investment_intelligence_service.domain.model.summary.InsightSelector;
import com.harsha.investment_intelligence_service.domain.model.summary.StrategySummary;
import com.harsha.investment_intelligence_service.domain.model.summary.TransitionSummary;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ContextAssembler {
    private final StrategySummaryBuilder strategySummaryBuilder;
    private final TransitionSummaryBuilder transitionSummaryBuilder;

    public ContextAssembler(
            StrategySummaryBuilder strategySummaryBuilder,
            TransitionSummaryBuilder transitionSummaryBuilder
    ) {
        this.strategySummaryBuilder = strategySummaryBuilder;
        this.transitionSummaryBuilder = transitionSummaryBuilder;
    }

    public AIReasoningContext assemble(
            SymbolContext context
    ) {

        StrategySummary strategySummary = strategySummaryBuilder.build(
                context.strategyHistory()
        );

        TransitionSummary transitionSummary = transitionSummaryBuilder.build(
                context.transitionHistory()
        );

        List<MarketInsightGeneratedEvent> insights = InsightSelector.topInsights(
                context.activeInsights(),
                5
        );

        List<StrategyEvaluationCompletedEvent> recentEvaluations =
                context.strategyHistory()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        StrategyEvaluationCompletedEvent::occurredAt
                                ).reversed()
                        )
                        .limit(5)
                        .toList();

        List<OpportunityTransitionEvent> recentTransitions =
                context.transitionHistory()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        OpportunityTransitionEvent::occurredAt
                                ).reversed()
                        )
                        .limit(5)
                        .toList();

        return new AIReasoningContext(
                context.symbol(),
                context.currentMarketSnapshot().company(),
                context.currentMarketSnapshot(),
                strategySummary,
                transitionSummary,
                insights,
                recentEvaluations,
                recentTransitions
        );
    }
}
