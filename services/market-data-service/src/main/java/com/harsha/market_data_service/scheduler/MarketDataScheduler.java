package com.harsha.market_data_service.scheduler;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.market_data_service.collector.MarketDataCollector;
import com.harsha.market_data_service.diff.MarketSnapshotDiffEngine;
import com.harsha.market_data_service.diff.StockTickDiffEngine;
import com.harsha.market_data_service.parser.MarketDataParser;
import com.harsha.market_data_service.publisher.MarketSnapshotPublisher;
import com.harsha.market_data_service.publisher.StockTickPublisher;
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
    private final StockTickDiffEngine stockTickDiffEngine;
    private final StockTickPublisher stockTickPublisher;
    private final MarketSnapshotPublisher marketSnapshotPublisher;
    private final MarketSnapshotDiffEngine marketSnapshotDiffEngine;
    private static final Logger log = LoggerFactory.getLogger(MarketDataScheduler.class);

    public MarketDataScheduler(
            MarketDataCollector collector,
            MarketDataParser parser,
            MarketDataTransformer transformer,
            StockTickDiffEngine stockTickDiffEngine,
            StockTickPublisher stockTickPublisher,
            MarketSnapshotPublisher marketSnapshotPublisher,
            MarketSnapshotDiffEngine marketSnapshotDiffEngine
    ) {
        this.collector = collector;
        this.parser = parser;
        this.transformer = transformer;
        this.stockTickDiffEngine = stockTickDiffEngine;
        this.stockTickPublisher = stockTickPublisher;
        this.marketSnapshotPublisher = marketSnapshotPublisher;
        this.marketSnapshotDiffEngine = marketSnapshotDiffEngine;
    }

    @Scheduled(fixedRate = 8000)
    public void run() {
        try {
            String raw = collector.fetchRawData();

            var response = parser.parse(raw);

            var stockTickEvents = transformer.toStockTickEvents(response);

            var marketSnapshotEvents = transformer.toMarketSnapshotEvents(response);

            if (stockTickEvents.isEmpty() && marketSnapshotEvents.isEmpty()) {
                log.warn("No stocks to process (watchlist empty or no matches)");
                return;
            }

            for (StockTickEvent event : stockTickEvents) {
                if (stockTickDiffEngine.hasChanged(event)) {
                    stockTickPublisher.publish(event);
                    log.info("PUBLISH STOCK_TICK_EVENT → {}", event);
                }
            }

            for (MarketSnapshotEvent event : marketSnapshotEvents) {
                if (marketSnapshotDiffEngine.hasChanged(event)) {
                    marketSnapshotPublisher.publish(event);
                    log.info("PUBLISH MARKET_SNAPSHOT_EVENT → {}", event);
                }
            }


        } catch (Exception e) {
            log.error("Market data pipeline failed", e);
        }
    }
}
