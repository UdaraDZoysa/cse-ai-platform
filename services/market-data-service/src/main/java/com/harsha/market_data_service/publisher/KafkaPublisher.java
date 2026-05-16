package com.harsha.market_data_service.publisher;

import com.harsha.contracts.messaging.EventEnvelope;
import com.harsha.contracts.events.market.StockTickEvent;
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
public class KafkaPublisher {
    private final KafkaTemplate<String, EventEnvelope<StockTickEvent>> kafkaTemplate;

    public KafkaPublisher(
            KafkaTemplate<String, EventEnvelope<StockTickEvent>> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(StockTickEvent stockTickEvent) {
        EventEnvelope<StockTickEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID().toString(),
                stockTickEvent.symbol(),
                EventType.STOCK_TICK_EVENT,
                EventVersions.V1,
                "market-data-service",
                Instant.now().toEpochMilli(),
                stockTickEvent
        );

        kafkaTemplate.send(KafkaTopics.MARKET_TICKS_V1, stockTickEvent.symbol(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event for symbol={} error={}",
                                stockTickEvent.symbol(), ex.getMessage(), ex);
                    } else {
                        log.debug("Event sent: symbol={} partition={} offset={}",
                                stockTickEvent.symbol(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
