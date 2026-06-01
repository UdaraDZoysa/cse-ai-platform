package com.harsha.strategy_service.application.detector;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.strategy_service.domain.model.detector.DetectorSignal;

public interface Detector {
    DetectorSignal detect(
            StockFeatureEvent event
    );
}
