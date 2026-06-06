package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.context.updater.MarketContextUpdater;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MarketSnapshotEventHandler implements EventHandler<MarketSnapshotEvent>{
    private final IdempotencyService idempotencyService;
    private final MarketContextUpdater marketContextUpdater;
    private static final Logger log = LoggerFactory.getLogger(MarketSnapshotEventHandler.class);


    public MarketSnapshotEventHandler(
            IdempotencyService idempotencyService,
            MarketContextUpdater marketContextUpdater
    ) {
        this.idempotencyService = idempotencyService;
        this.marketContextUpdater = marketContextUpdater;
    }

    @Override
    public EventType eventType() {
        return EventType.MARKET_SNAPSHOT_EVENT;
    }

    @Override
    public Class<MarketSnapshotEvent> eventClass() {
        return MarketSnapshotEvent.class;
    }

    @Override
    public void handle(String eventId, MarketSnapshotEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }

        marketContextUpdater.update(event);

        log.info(
                """
                Market context updated.
                
                Symbol: {}
                Timestamp: {}
                
                """,
                event.symbol(),
                Instant.now()
        );

        idempotencyService.markProcessed(eventId);
    }
}
