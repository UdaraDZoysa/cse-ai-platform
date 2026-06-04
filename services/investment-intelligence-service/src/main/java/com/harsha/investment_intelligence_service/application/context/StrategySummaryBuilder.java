package com.harsha.investment_intelligence_service.application.context;

import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.investment_intelligence_service.domain.model.summary.StrategySummary;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.List;

@Component
public class StrategySummaryBuilder {
    private static final int TREND_WINDOW = 10;

    public StrategySummary build(
            Deque<StrategyEvaluationCompletedEvent> history
    ) {
        if (history.isEmpty()) {
            return null;
        }

        List<StrategyEvaluationCompletedEvent> evaluations = List.copyOf(history);

        StrategyEvaluationCompletedEvent latest = evaluations.get(
                evaluations.size() - 1
        );

        double averageConfidence = evaluations.stream()
                .mapToDouble(
                        StrategyEvaluationCompletedEvent::confidence
                )
                .average()
                .orElse(0);

        double confidenceTrend = calculateTrend(evaluations);

        double confidenceVolatility = calculateVolatility(
                evaluations,
                averageConfidence
        );

        return new StrategySummary(
                latest.confidence(),
                averageConfidence,
                confidenceTrend,
                confidenceVolatility,
                latest.persistence(),
                latest.status(),
                latest.marketRegime()
        );
    }

    private double calculateTrend(
            List<StrategyEvaluationCompletedEvent> evaluations
    ) {
        int size = evaluations.size();

        int startIndex = Math.max(
                0,
                size - TREND_WINDOW
        );

        double startConfidence = evaluations.get(startIndex).confidence();

        double endConfidence = evaluations.get(size - 1).confidence();

        return endConfidence - startConfidence;
    }

    private double calculateVolatility(
            List<StrategyEvaluationCompletedEvent> evaluations,
            double mean
    ) {
        double variance = evaluations.stream()
                .mapToDouble(
                        evaluation ->
                                Math.pow(evaluation.confidence() - mean, 2)
                        )
                        .average()
                        .orElse(0);

        return Math.sqrt(variance);
    }
}
