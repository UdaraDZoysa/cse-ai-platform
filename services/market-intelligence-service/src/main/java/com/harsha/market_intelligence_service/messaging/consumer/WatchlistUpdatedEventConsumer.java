package com.harsha.market_intelligence_service.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.events.common.WatchlistUpdatedEvent;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.messaging.KafkaTopics;
import com.harsha.market_intelligence_service.filtering.service.TrackedSymbolService;
import com.harsha.market_intelligence_service.orchestration.service.WatchlistOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WatchlistUpdatedEventConsumer {
    private final ObjectMapper objectMapper;
    private final TrackedSymbolService trackedSymbolService;
    private final WatchlistOrchestrator orchestrator;
    private static final Logger log = LoggerFactory.getLogger(WatchlistUpdatedEventConsumer.class);

    public WatchlistUpdatedEventConsumer(
            TrackedSymbolService trackedSymbolService,
            ObjectMapper objectMapper,
            WatchlistOrchestrator orchestrator) {
        this.trackedSymbolService = trackedSymbolService;
        this.objectMapper = objectMapper;
        this.orchestrator = orchestrator;
    }

    @KafkaListener(topics = KafkaTopics.WATCHLIST_UPDATED_EVENT_V1, groupId = "${spring.kafka.consumer.group-id}")
    public void handle(EventEnvelope<WatchlistUpdatedEvent> envelope) {
        if (envelope.payload() == null) {
            return;
        }

        try {
            WatchlistUpdatedEvent event = objectMapper.convertValue(
                    envelope.payload(),
                    WatchlistUpdatedEvent.class
            );

            var snapshot = trackedSymbolService.handleWatchlistUpdate(event);

            orchestrator.handleWatchlistUpdate(snapshot);

            log.info(
                    "Received watchlist update: {}",
                    event.symbols()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process watchlist update event",
                    e
            );
        }
    }
}
