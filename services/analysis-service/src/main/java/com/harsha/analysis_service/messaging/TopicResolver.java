package com.harsha.analysis_service.messaging;

import org.springframework.stereotype.Component;

@Component
public class TopicResolver {
    private final KafkaTopics topics;

    public TopicResolver(
            KafkaTopics topics
    ) {
        this.topics = topics;
    }

    public String resolveTopic(
            String eventType,
            TopicType topicType
    ) {
        return switch (eventType) {
            case "STOCK_FEATURE_EVENT" ->
                switch (topicType) {
                    case MAIN ->
                            topics.getStockFeatureTopic();

                    case DLT ->
                        topics.getDltStockFeatureTopic();
                };

            case "STOCK_TICK_EVENT" ->
                    switch (topicType) {
                        case MAIN ->
                                topics.getMarketTicksTopic();

                        case DLT ->
                                topics.getDltMarketTicksTopic();
                    };

            default ->
                    throw new IllegalArgumentException(
                            "Unknown event type: " + eventType
                    );
        };
    }
}
