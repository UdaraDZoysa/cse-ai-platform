package com.harsha.market_data_service.publisher;

import com.harsha.events.core.EventEnvelope;
import com.harsha.events.market.StockTickEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class KafkaPublisher {
    private final KafkaTemplate<String, EventEnvelope<StockTickEvent>> kafkaTemplate;
    private final KafkaTopics topics;

    public KafkaPublisher(
            KafkaTemplate<String, EventEnvelope<StockTickEvent>> kafkaTemplate,
            KafkaTopics topics
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    public void publish(StockTickEvent stockTickEvent) {
        EventEnvelope<StockTickEvent> envelope = new EventEnvelope<>(
                UUID.randomUUID().toString(),
                stockTickEvent.symbol(),
                "STOCK_TICK_EVENT",
                "market-data-service",
                Instant.now().toEpochMilli(),
                stockTickEvent
        );

        kafkaTemplate.send(topics.getStockTicksTopic(), stockTickEvent.symbol(), envelope)
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
