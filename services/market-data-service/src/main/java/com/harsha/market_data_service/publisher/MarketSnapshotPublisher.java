package com.harsha.market_data_service.publisher;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.messaging.EventType;
import com.harsha.contracts.messaging.KafkaTopics;
import com.harsha.contracts.versions.EventVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class MarketSnapshotPublisher {
    private final KafkaTemplate<String, EventEnvelope<MarketSnapshotEvent>> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(MarketSnapshotPublisher.class);

    public MarketSnapshotPublisher(
            KafkaTemplate<String, EventEnvelope<MarketSnapshotEvent>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(MarketSnapshotEvent event) {
        try {
            EventEnvelope<MarketSnapshotEvent> envelope = new EventEnvelope<>(
                    UUID.randomUUID().toString(),
                    event.symbol(),
                    EventType.MARKET_SNAPSHOT_EVENT,
                    EventVersions.V1,
                    "market-data-service",
                    Instant.now().toEpochMilli(),
                    event
            );
            kafkaTemplate.send(KafkaTopics.MARKET_SNAPSHOT_EVENT_V1, event.symbol(), envelope)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event for symbol={} error={}",
                                    event.symbol(),
                                    ex.getMessage(),
                                    ex
                            );
                            return;
                        }
                        log.debug(
                                """
                                Watchlist Updated Event published successfully.
                                Symbol={}
                                topic={}
                                partition={}
                                offset={}
                                """,
                                event.symbol(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    });
        } catch (Exception ex) {
            log.error(
                    """
                    Unexpected synchronous failure while publishing Watchlist Updated Event.
                    Symbol={}
                    error={}
                    """,
                    event.symbol(),
                    ex.getMessage(),
                    ex
            );
        }
    }
}
