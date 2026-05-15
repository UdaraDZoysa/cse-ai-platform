package com.harsha.contracts.messaging;

public enum EventType {
    STOCK_TICK_EVENT(
            KafkaTopics.MARKET_TICKS_V1,
            KafkaTopics.MARKET_TICKS_DLT_V1
    ),

    STOCK_FEATURE_EVENT(
            KafkaTopics.STOCK_FEATURES_V1,
            KafkaTopics.STOCK_FEATURES_DLT_V1
    );

    private final String mainTopic;
    private final String dltTopic;

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
}
