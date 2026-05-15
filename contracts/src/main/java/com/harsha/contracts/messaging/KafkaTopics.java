package com.harsha.contracts.messaging;

public final class KafkaTopics {
    private KafkaTopics() {}

    public static final String MARKET_TICKS_V1 =
            "market.stock-ticks.v1";

    public static final String MARKET_TICKS_DLT_V1 =
            "market.stock-ticks.dlt.v1";

    public static final String STOCK_FEATURES_V1 =
            "market.stock-features.v1";

    public static final String STOCK_FEATURES_DLT_V1 =
            "market.stock-features.dlt.v1";

}
