package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import com.harsha.investment_intelligence_service.domain.repository.MarketInsightHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MarketInsightGenEventHandler implements EventHandler<MarketInsightGeneratedEvent>{
    private final IdempotencyService idempotencyService;
    private final MarketInsightHistoryRepository repository;
    private static final Logger log = LoggerFactory.getLogger(MarketInsightGenEventHandler.class);

    public MarketInsightGenEventHandler(
            IdempotencyService idempotencyService,
            MarketInsightHistoryRepository repository
    ) {
        this.idempotencyService = idempotencyService;
        this.repository = repository;
    }

    @Override
    public EventType eventType() {
        return EventType.MARKET_INSIGHT_GENERATED_EVENT;
    }

    @Override
    public Class<MarketInsightGeneratedEvent> eventClass() {
        return MarketInsightGeneratedEvent.class;
    }

    @Override
    public void handle(String eventId, MarketInsightGeneratedEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }

        repository.save(event);

        log.info(
                """
                Market Insight context updated.
                
                Symbol: {}
                Timestamp: {}
                
                """,
                event.symbol(),
                Instant.now()
        );

        idempotencyService.markProcessed(eventId);
    }
}
