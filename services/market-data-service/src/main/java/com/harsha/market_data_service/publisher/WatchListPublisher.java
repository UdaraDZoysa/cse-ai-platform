package com.harsha.market_data_service.publisher;

import com.harsha.contracts.events.common.WatchlistUpdatedEvent;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.messaging.EventType;
import com.harsha.contracts.messaging.KafkaTopics;
import com.harsha.contracts.versions.EventVersions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class WatchListPublisher {
    private final KafkaTemplate<String, EventEnvelope<WatchlistUpdatedEvent>> kafkaTemplate;

    public WatchListPublisher(
            KafkaTemplate<String, EventEnvelope<WatchlistUpdatedEvent>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(WatchlistUpdatedEvent event) {
        try {
            EventEnvelope<WatchlistUpdatedEvent> envelope = new EventEnvelope<>(
                    UUID.randomUUID().toString(),
                    event.watchlistId(),
                    EventType.WATCHLIST_UPDATED_EVENT,
                    EventVersions.V1,
                    "market-data-service",
                    Instant.now().toEpochMilli(),
                    event
            );
            kafkaTemplate.send(KafkaTopics.WATCHLIST_UPDATED_EVENT_V1, event.watchlistId(), envelope)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event for WatchList Id={} error={}",
                                    event.watchlistId(),
                                    ex.getMessage(),
                                    ex
                            );
                            return;
                        }
                        log.debug(
                                """
                                Watchlist Updated Event published successfully.
                                WatchList Id={}
                                topic={}
                                partition={}
                                offset={}
                                """,
                                event.watchlistId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    });
        } catch (Exception ex) {
            log.error(
                    """
                    Unexpected synchronous failure while publishing Watchlist Updated Event.
                    WatchList Id={}
                    error={}
                    """,
                    event.watchlistId(),
                    ex.getMessage(),
                    ex
            );
        }
    }
}
