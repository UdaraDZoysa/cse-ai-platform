package com.harsha.strategy_service.application.detector;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.events.analysis.TrendDirection;
import com.harsha.strategy_service.domain.model.DetectorSignal;
import com.harsha.strategy_service.domain.model.DetectorType;
import com.harsha.contracts.events.strategy.SignalDirection;
import org.springframework.stereotype.Component;

@Component
public class TrendDetector implements Detector {

    @Override
    public DetectorSignal detect(StockFeatureEvent event) {
        var trend = event.trend();

        if (Double.isNaN(trend.persistence())) {
            return DetectorSignal.invalid(
                    DetectorType.TREND
            );
        }

        double imbalance = Math.abs(
                trend.downwardRatio() - trend.downwardRatio()
        );

        double strength = imbalance * trend.persistence();

        if (trend.direction() == TrendDirection.BULLISH) {
            return new DetectorSignal(
                    DetectorType.TREND,
                    strength,
                    0.85,
                    SignalDirection.BULLISH,
                    true
            );
        }
        if (trend.direction() == TrendDirection.BEARISH) {
            return new DetectorSignal(
                    DetectorType.TREND,
                    strength,
                    0.85,
                    SignalDirection.BEARISH,
                    true
            );
        }

        return new DetectorSignal(
                DetectorType.TREND,
                0,
                0.5,
                SignalDirection.NEUTRAL,
                true
        );

    }
}
