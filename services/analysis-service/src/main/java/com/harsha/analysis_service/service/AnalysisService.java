package com.harsha.analysis_service.service;

import com.harsha.analysis_service.service.feature.FeatureExtractor;
import com.harsha.analysis_service.service.signal.SignalEngine;
import com.harsha.events.market.StockFeatureEvent;
import com.harsha.events.market.StockTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.harsha.events.market.TrendFeatures;
import com.harsha.events.market.MomentumFeatures;
import com.harsha.events.market.VolatilityFeatures;
import com.harsha.events.market.MovingAverageFeatures;

@Service
public class AnalysisService {
    private final FeatureExtractor featureExtractor;
    private final SignalEngine signalEngine;
    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    public AnalysisService(
            FeatureExtractor featureExtractor,
            SignalEngine signalEngine) {
        this.featureExtractor = featureExtractor;
        this.signalEngine = signalEngine;
    }

    public StockFeatureEvent analyseEvent(StockTickEvent event) {
        var features = featureExtractor.extract(event);
        if (features == null) {
            log.info("First Observation → {}", event);
            return null;
        }
//        var signal = signalEngine.evaluates(features);
//        if (signal != null) {
//            log.info("SIGNAL → {}", signal);
//        }

        return new StockFeatureEvent(
                features.symbol(),
                features.occurredAt(),
                features.trend(),
                features.momentum(),
                features.volatility(),
                features.movingAverage()
        );
    }
}
