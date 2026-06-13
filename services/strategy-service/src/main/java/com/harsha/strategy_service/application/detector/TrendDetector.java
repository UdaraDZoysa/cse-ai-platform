package com.harsha.strategy_service.application.detector;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.events.analysis.TrendDirection;
import com.harsha.strategy_service.domain.model.detector.DetectorSignal;
import com.harsha.strategy_service.domain.model.detector.DetectorType;
import com.harsha.contracts.events.strategy.SignalDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrendDetector implements Detector {
    private static final Logger log = LoggerFactory.getLogger(TrendDetector.class);


    @Override
    public DetectorSignal detect(StockFeatureEvent event) {
        var trend = event.trend();

        if (Double.isNaN(trend.persistence())) {
            return DetectorSignal.invalid(
                    DetectorType.TREND
            );
        }

        double imbalance = Math.abs(
                trend.upwardRatio() - trend.downwardRatio()
        );

        double strength = (imbalance + trend.persistence()) / 2.0;

        if (trend.direction() == TrendDirection.BULLISH) {
            log.info(
                    """
                    TREND DEBUG
                    symbol={}
                    direction={}
                    upwardRatio={}
                    downwardRatio={}
                    persistence={}
                    imbalance={}
                    strength={}
                    """,
                    event.symbol(),
                    trend.direction(),
                    trend.upwardRatio(),
                    trend.downwardRatio(),
                    trend.persistence(),
                    imbalance,
                    strength
            );

            return new DetectorSignal(
                    DetectorType.TREND,
                    strength,
                    0.85,
                    SignalDirection.BULLISH,
                    true
            );
        }
        if (trend.direction() == TrendDirection.BEARISH) {
            log.info(
                    """
                    TREND DEBUG
                    symbol={}
                    direction={}
                    upwardRatio={}
                    downwardRatio={}
                    persistence={}
                    imbalance={}
                    strength={}
                    """,
                    event.symbol(),
                    trend.direction(),
                    trend.upwardRatio(),
                    trend.downwardRatio(),
                    trend.persistence(),
                    imbalance,
                    strength
            );

            return new DetectorSignal(
                    DetectorType.TREND,
                    strength,
                    0.85,
                    SignalDirection.BEARISH,
                    true
            );
        }

        log.info(
                """
                TREND DEBUG
                symbol={}
                direction={}
                upwardRatio={}
                downwardRatio={}
                persistence={}
                imbalance={}
                strength={}
                """,
                event.symbol(),
                trend.direction(),
                trend.upwardRatio(),
                trend.downwardRatio(),
                trend.persistence(),
                imbalance,
                strength
        );

        return new DetectorSignal(
                DetectorType.TREND,
                strength,
                0.5,
                SignalDirection.NEUTRAL,
                true
        );

    }
}
