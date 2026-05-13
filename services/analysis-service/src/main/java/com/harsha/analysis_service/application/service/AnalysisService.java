package com.harsha.analysis_service.application.service;

import com.harsha.analysis_service.application.service.feature.FeatureExtractor;
import com.harsha.analysis_service.application.service.feature.model.StockFeatureSnapshot;
import com.harsha.analysis_service.application.service.persistence.FeatureSnapshotMapper;
import com.harsha.analysis_service.application.service.persistence.FeatureSnapshotService;
import com.harsha.analysis_service.application.service.signal.SignalEngine;
import com.harsha.events.market.StockFeatureEvent;
import com.harsha.events.market.StockTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {
    private final FeatureExtractor featureExtractor;
    private final FeatureSnapshotMapper mapper;
    private final FeatureSnapshotService snapshotService;

    private final SignalEngine signalEngine;
    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    public AnalysisService(
            FeatureExtractor featureExtractor,
            FeatureSnapshotMapper mapper,
            FeatureSnapshotService snapshotService,
            SignalEngine signalEngine) {
        this.featureExtractor = featureExtractor;
        this.mapper = mapper;
        this.snapshotService = snapshotService;
        this.signalEngine = signalEngine;
    }

    public StockFeatureEvent analyseEvent(
            String eventId,
            StockTickEvent event
    ) {
        StockFeatureSnapshot snapshot = featureExtractor.extract(event);
        if (snapshot == null) {
            log.info("First Observation → {}", event);
            return null;
        }
//        var signal = signalEngine.evaluates(features);
//        if (signal != null) {
//            log.info("SIGNAL → {}", signal);
//        }

        var window = featureExtractor.currentWindow(event.symbol());

        var entity = mapper.map(eventId, window, snapshot);

        snapshotService.save(entity);

        StockFeatureEvent featureEvent =
                new StockFeatureEvent(
                        snapshot.symbol(),
                        snapshot.occurredAt(),
                        snapshot.trend(),
                        snapshot.momentum(),
                        snapshot.volatility(),
                        snapshot.movingAverage()
                );

        return featureEvent;
    }
}
