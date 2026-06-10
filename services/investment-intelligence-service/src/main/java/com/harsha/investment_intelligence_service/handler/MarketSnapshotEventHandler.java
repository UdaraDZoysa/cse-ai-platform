package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import com.harsha.investment_intelligence_service.domain.repository.MarketSnapshotHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MarketSnapshotEventHandler implements EventHandler<MarketSnapshotEvent>{
    private final IdempotencyService idempotencyService;
    private final MarketSnapshotHistoryRepository repository;
    private static final Logger log = LoggerFactory.getLogger(MarketSnapshotEventHandler.class);


    public MarketSnapshotEventHandler(
            IdempotencyService idempotencyService,
            MarketSnapshotHistoryRepository repository
    ) {
        this.idempotencyService = idempotencyService;
        this.repository = repository;
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

        repository.save(event);

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
