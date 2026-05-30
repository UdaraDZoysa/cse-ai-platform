package com.harsha.contracts.messaging;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum EventType {
    STOCK_TICK_EVENT(
            KafkaTopics.MARKET_TICKS_V1,
            KafkaTopics.MARKET_TICKS_DLT_V1
    ),

    STOCK_FEATURE_EVENT(
            KafkaTopics.STOCK_FEATURES_V1,
            KafkaTopics.STOCK_FEATURES_DLT_V1
    ),

    WATCHLIST_UPDATED_EVENT(
            KafkaTopics.WATCHLIST_UPDATED_EVENT_V1,
            KafkaTopics.WATCHLIST_UPDATED_EVENT_DLT_V1
    ),
    MARKET_INSIGHT_GENERATED_EVENT(
            KafkaTopics.MARKET_INSIGHT_GENERATED_EVENT_V1,
            KafkaTopics.MARKET_INSIGHT_GENERATED_EVENT_DLT_V1
    );

    private final String mainTopic;
    private final String dltTopic;

    private static final Map<String, EventType> TOPIC_MAP =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(
                            EventType::mainTopic,
                            Function.identity()
                    ));

    EventType(
            String mainTopic,
            String dltTopic
    ) {
        this.mainTopic = mainTopic;
        this.dltTopic = dltTopic;
    }

    public String mainTopic() {
        return mainTopic;
    }

    public String dltTopic() {
        return dltTopic;
    }

    //reverse topic lookup
    public static EventType fromTopic(String topic) {
        EventType eventType = TOPIC_MAP.get(topic);

        if (eventType == null) {
            throw new IllegalArgumentException(
                    "Unknown topic: " + topic
            );
        }

        return eventType;
    }
}
