package com.harsha.analysis_service.service.feature.pipeline;

import com.harsha.analysis_service.service.feature.calculator.momentum.MomentumCalculator;
import com.harsha.analysis_service.service.feature.calculator.movingaverage.MovingAverageCalculator;
import com.harsha.analysis_service.service.feature.calculator.trend.TrendCalculator;
import com.harsha.analysis_service.service.feature.calculator.volatility.VolatilityCalculator;
import com.harsha.analysis_service.service.feature.model.StockFeatureSnapshot;
import com.harsha.events.market.StockTickEvent;
import com.harsha.events.market.TrendFeatures;
import com.harsha.events.market.MomentumFeatures;
import com.harsha.events.market.VolatilityFeatures;
import com.harsha.events.market.MovingAverageFeatures;
import org.springframework.stereotype.Component;

import java.util.Deque;

@Component
public class FeaturePipeline {
    private final TrendCalculator trendCalculator;
    private final MomentumCalculator momentumCalculator;
    private final VolatilityCalculator volatilityCalculator;
    private final MovingAverageCalculator movingAverageCalculator;

    public FeaturePipeline(
            TrendCalculator trendCalculator,
            MomentumCalculator momentumCalculator,
            VolatilityCalculator volatilityCalculator,
            MovingAverageCalculator movingAverageCalculator
    ) {
        this.trendCalculator = trendCalculator;
        this.momentumCalculator = momentumCalculator;
        this.volatilityCalculator = volatilityCalculator;
        this.movingAverageCalculator = movingAverageCalculator;
    }

    public StockFeatureSnapshot build(
            String symbol,
            long occurredAt,
            Deque<StockTickEvent> window
    ) {
        return new StockFeatureSnapshot(
                symbol,
                occurredAt,
                trendCalculator.calculate(window),
                momentumCalculator.calculate(window),
                volatilityCalculator.calculate(window),
                movingAverageCalculator.calculate(window)
        );
    }
}
