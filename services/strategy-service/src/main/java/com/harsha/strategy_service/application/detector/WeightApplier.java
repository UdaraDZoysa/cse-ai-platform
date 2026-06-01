package com.harsha.strategy_service.application.detector;

import com.harsha.strategy_service.domain.model.detector.DetectorSignal;
import org.springframework.stereotype.Component;

@Component
public class WeightApplier {
    public DetectorSignal applyWeight(
            DetectorSignal signal,
            double weight
    ) {

        return new DetectorSignal(
                signal.source(),
                signal.strength() * weight,
                signal.reliability(),
                signal.direction(),
                signal.valid()
        );
    }
}
