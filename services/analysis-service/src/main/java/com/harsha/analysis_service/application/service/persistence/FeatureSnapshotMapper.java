package com.harsha.analysis_service.application.service.persistence;

import com.harsha.analysis_service.application.service.evaluator.model.MarketEvaluationResult;
import com.harsha.analysis_service.application.service.feature.model.StockFeatureSnapshot;
import com.harsha.analysis_service.persistence.entity.StockFeatureSnapshotEntity;
import com.harsha.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.Deque;

@Component
public class FeatureSnapshotMapper {
    public StockFeatureSnapshotEntity map(
            String latestEventId,
            Deque<StockTickEvent> window,
            StockFeatureSnapshot snapshot,
            MarketEvaluationResult evaluationResult
    ) {
        StockTickEvent latestTick = window.getLast();

        return StockFeatureSnapshotEntity.builder()
                .symbol(snapshot.symbol())

                .windowStartTime(window.getFirst().lastTradedTime())
                .windowEndTime(window.getLast().lastTradedTime())
                .windowSize(window.size())
                .latestEventId(latestEventId)

                //current market state
                .price(latestTick.price())
                .change(latestTick.change())
                .volume(latestTick.volume())
                .high(latestTick.high())
                .low(latestTick.low())

                //trend
                .upwardRatio(snapshot.trend().upwardRatio())
                .downwardRatio(snapshot.trend().downwardRatio())
                .trendPersistence(snapshot.trend().persistence())
                .trendDirection(snapshot.trend().direction())

                //momentum
                .cumulativeReturn(snapshot.momentum().cumulativeReturn())
                .averageReturn(snapshot.momentum().averageReturn())
                .returnStdDev(snapshot.momentum().returnStdDev())
                .averageDelta(snapshot.momentum().averageDelta())
                .acceleration(snapshot.momentum().acceleration())
                .positiveMoveRatio(snapshot.momentum().positiveMoveRatio())
                .negativeMoveRatio(snapshot.momentum().negativeMoveRatio())
                .momentumPersistence(snapshot.momentum().momentumPersistence())
                .largestUpMove(snapshot.momentum().largestUpMove())
                .largestDownMove(snapshot.momentum().largestDownMove())
                .efficiencyRatio(snapshot.momentum().efficiencyRatio())

                //volatility
                .volatilityStdDev(snapshot.volatility().standardDeviation())
                .volatilityRegime(snapshot.volatility().regime())
                .volatilityVariance(snapshot.volatility().variance())

                //MA
                .sma5(snapshot.movingAverage().sma5())
                .sma20(snapshot.movingAverage().sma20())
                .ema5(snapshot.movingAverage().ema5())
                .ema20(snapshot.movingAverage().ema20())

                //significance metadata
                .significanceScore(evaluationResult.significanceScore())
                .confidence(evaluationResult.confidence())
                .marketRegime(evaluationResult.marketRegime())

                .build();
    }
}
