package com.harsha.analysis_service.application.service;

import com.harsha.analysis_service.application.events.EventPublisher;
import com.harsha.analysis_service.application.service.evaluator.MarketStateEvaluator;
import com.harsha.analysis_service.application.service.evaluator.model.MarketEvaluationResult;
import com.harsha.analysis_service.application.service.feature.FeatureExtractor;
import com.harsha.analysis_service.application.service.feature.model.StockFeatureSnapshot;
import com.harsha.analysis_service.application.service.persistence.FeatureSnapshotMapper;
import com.harsha.analysis_service.application.service.persistence.FeatureSnapshotService;
import com.harsha.contracts.events.market.StockFeatureEvent;
import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.contracts.messaging.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {
    private final FeatureExtractor featureExtractor;
    private final FeatureSnapshotMapper mapper;
    private final FeatureSnapshotService snapshotService;
    private final MarketStateEvaluator marketStateEvaluator;
    private final EventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    public AnalysisService(
            FeatureExtractor featureExtractor,
            FeatureSnapshotMapper mapper,
            FeatureSnapshotService snapshotService,
            MarketStateEvaluator marketStateEvaluator,
            EventPublisher eventPublisher
    ) {
        this.featureExtractor = featureExtractor;
        this.mapper = mapper;
        this.snapshotService = snapshotService;
        this.marketStateEvaluator = marketStateEvaluator;
        this.eventPublisher = eventPublisher;
    }

    public void analyseEvent(
            String eventId,
            StockTickEvent event
    ) {
        StockFeatureSnapshot snapshot = featureExtractor.extract(event);
        if (snapshot == null) {
            log.info("First Observation → {}", event);
            return;
        }

        MarketEvaluationResult evaluation =
                marketStateEvaluator.evaluate(snapshot);

        if (evaluation.persist()){
            var window = featureExtractor.currentWindow(event.symbol());

            var entity = mapper.map(eventId, window, snapshot, evaluation);

            snapshotService.save(entity);
        }

        if (evaluation.publish()) {

            StockFeatureEvent featureEvent =
                    new StockFeatureEvent(
                            snapshot.symbol(),
                            snapshot.occurredAt(),
                            snapshot.trend(),
                            snapshot.momentum(),
                            snapshot.volatility(),
                            snapshot.movingAverage()
                    );

            eventPublisher.publish(
                    snapshot.symbol(),
                    EventType.STOCK_FEATURE_EVENT,
                    featureEvent);
        }
    }
}
