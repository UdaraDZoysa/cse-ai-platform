package com.harsha.strategy_service.application.detector;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.events.analysis.VolatilityRegime;
import com.harsha.strategy_service.domain.model.DetectorSignal;
import com.harsha.strategy_service.domain.model.DetectorType;
import com.harsha.strategy_service.domain.model.SignalDirection;
import org.springframework.stereotype.Component;

@Component
public class VolatilityDetector implements Detector {

    @Override
    public DetectorSignal detect(StockFeatureEvent event) {
        var volatility =
                event.volatility();

        if (Double.isNaN(volatility.standardDeviation())) {
            return DetectorSignal.invalid(
                    DetectorType.VOLATILITY
            );
        }

        double risk;

        VolatilityRegime regime = volatility.regime();

        switch (regime) {
            case LOW -> risk = 0.2;

            case MEDIUM -> risk = 0.5;

            case HIGH -> risk = 0.8;

            case EXTREME -> risk = 1.0;

            default -> risk = 0;
        }

        return new DetectorSignal(
                DetectorType.VOLATILITY,
                risk,
                0.9,
                SignalDirection.NEUTRAL,
                true
        );
    }
}
