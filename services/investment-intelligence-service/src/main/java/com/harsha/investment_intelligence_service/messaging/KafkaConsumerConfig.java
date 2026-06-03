package com.harsha.investment_intelligence_service.messaging;

import com.harsha.contracts.messaging.EventType;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recover =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> {
                            EventType eventType =
                                    EventType.fromTopic(
                                            record.topic()
                                    );
                            return new TopicPartition(
                                    eventType.dltTopic(),
                                    record.partition()
                            );
                        }
                );
        FixedBackOff backOff = new FixedBackOff(2000L, 3);
        return new DefaultErrorHandler(recover, backOff);
    }
}
