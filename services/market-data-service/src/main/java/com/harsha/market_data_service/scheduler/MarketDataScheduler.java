package com.harsha.market_data_service.scheduler;

import com.harsha.events.market.StockTickEvent;
import com.harsha.market_data_service.collector.MarketDataCollector;
import com.harsha.market_data_service.diff.MarketDataDiffEngine;
import com.harsha.market_data_service.feature.FeatureExtractor;
import com.harsha.market_data_service.parser.MarketDataParser;
import com.harsha.market_data_service.publisher.KafkaPublisher;
import com.harsha.market_data_service.service.MarketDataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class MarketDataScheduler {
    private final MarketDataCollector collector;
    private final MarketDataParser parser;
    private final MarketDataTransformer transformer;
    private final MarketDataDiffEngine diffEngine;
    //private final FeatureExtractor featureExtractor;
    //private final SignalEngine signalEngine;
    private final KafkaPublisher publisher;
    private static final Logger log = LoggerFactory.getLogger(MarketDataScheduler.class);

    public MarketDataScheduler(
            MarketDataCollector collector,
            MarketDataParser parser,
            MarketDataTransformer transformer,
            MarketDataDiffEngine diffEngine,
            KafkaPublisher publisher
    ) {
        this.collector = collector;
        this.parser = parser;
        this.transformer = transformer;
        this.diffEngine = diffEngine;
        this.publisher = publisher;
    }

    @Scheduled(fixedRate = 8000)
    public void run() {
        try {
            String raw = collector.fetchRawData();

            var response = parser.parse(raw);

            var events = transformer.toEvents(response);

            if (events.isEmpty()) {
                log.warn("No stocks to process (watchlist empty or no matches)");
                return;
            }

            for (StockTickEvent event : events) {
                if (diffEngine.hasChanged(event)) {
                    publisher.publish(event);
                    log.info("PUBLISH EVENT → {}", event);
                }

//                var features = featureExtractor.extract(event);
//                if (features == null) {
//                    log.info("First Observation → {}", event);
//                    continue; // skip first observation
//                }
//
//                var signal = signalEngine.evaluates(features);
//                if (signal != null) {
//                    log.info("SIGNAL → {}", signal);
//                }
            }
        } catch (Exception e) {
            log.error("Market data pipeline failed", e);
        }
    }
}
