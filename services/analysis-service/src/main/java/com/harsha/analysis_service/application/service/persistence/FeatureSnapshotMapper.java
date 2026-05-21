package com.harsha.analysis_service.application.service.persistence;

import com.harsha.analysis_service.application.service.evaluator.model.MarketEvaluationResult;
import com.harsha.analysis_service.application.service.feature.model.StockFeatureSnapshot;
import com.harsha.analysis_service.persistence.entity.StockFeatureSnapshotEntity;
import com.harsha.contracts.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.Deque;

@Component
public class FeatureSnapshotMapper {
    public StockFeatureSnapshotEntity map(
            String latestEventId,
            Deque<StockTickEvent> window,
            StockFeatureSnapshot snapshot
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
                .acceleration(snapshot.momentum().acceleration())
                .positiveMoveRatio(snapshot.momentum().positiveMoveRatio())
                .negativeMoveRatio(snapshot.momentum().negativeMoveRatio())
                .momentumPersistence(snapshot.momentum().momentumPersistence())
                .largestUpMove(snapshot.momentum().largestPositiveReturn())
                .largestDownMove(snapshot.momentum().largestNegativeReturn())
                .efficiencyRatio(snapshot.momentum().efficiencyRatio())

                //volatility
                .volatilityStdDev(snapshot.volatility().standardDeviation())
                .volatilityRegime(snapshot.volatility().regime())
                .volatilityVariance(snapshot.volatility().variance())

                //MA
                .sma5Tick(snapshot.movingAverage().sma5Tick())
                .sma20Tick(snapshot.movingAverage().sma20Tick())
                .ema5Tick(snapshot.movingAverage().ema5Tick())
                .ema20Tick(snapshot.movingAverage().ema20Tick())

                .build();
    }
}
