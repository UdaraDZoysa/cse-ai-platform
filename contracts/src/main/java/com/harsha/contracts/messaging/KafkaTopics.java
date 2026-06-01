package com.harsha.contracts.messaging;

import java.security.PublicKey;

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

    public static final String WATCHLIST_UPDATED_EVENT_V1 =
            "market.watchlist-updated-event.v1";

    public static final String WATCHLIST_UPDATED_EVENT_DLT_V1 =
            "market.watchlist-updated-event.dlt.v1";

    public static final String MARKET_INSIGHT_GENERATED_EVENT_V1 =
            "market.insight-generated-event.v1";

    public static final String MARKET_INSIGHT_GENERATED_EVENT_DLT_V1 =
            "market.insight-generated-event.dlt.v1";

    public static final String STRATEGY_EVALUATION_COMPLETED_EVENT_V1 =
            "market.evaluation-completed-event.v1";

    public static final String STRATEGY_EVALUATION_COMPLETED_EVENT_DLT_V1 =
            "market.evaluation-completed-event.dlt.v1";

    public static final String OPPORTUNITY_TRANSITION_EVENT_V1 =
            "market.opportunity-transition-event.v1";

    public static final String OPPORTUNITY_TRANSITION_EVENT_DLT_V1 =
            "market.opportunity-transition-event.dlt.v1";
}
