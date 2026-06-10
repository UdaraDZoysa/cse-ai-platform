package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.common.WatchlistUpdatedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import com.harsha.investment_intelligence_service.domain.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class WatchlistUpdatedEventHandler implements EventHandler<WatchlistUpdatedEvent> {
    private final IdempotencyService idempotencyService;
    private final WatchlistRepository repository;
    private static final Logger log = LoggerFactory.getLogger(WatchlistUpdatedEventHandler.class);

    public WatchlistUpdatedEventHandler(
            IdempotencyService idempotencyService,
            WatchlistRepository repository
    ) {
        this.idempotencyService = idempotencyService;
        this.repository = repository;
    }

    @Override
    public EventType eventType() {
        return EventType.WATCHLIST_UPDATED_EVENT;
    }

    @Override
    public Class<WatchlistUpdatedEvent> eventClass() {
        return WatchlistUpdatedEvent.class;
    }

    @Override
    public void handle(String eventId, WatchlistUpdatedEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }

        repository.save(event);

        log.info(
                """
                Watchlist updated.
                
                Symbols: {}
                Timestamp: {}
                
                """,
                event.symbols(),
                Instant.now()
        );

        idempotencyService.markProcessed(eventId);
    }
}
