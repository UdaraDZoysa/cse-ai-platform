package com.harsha.analysis_service.messaging;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaTopics {
    @Value("${topic.analysis.stock.features}")
    private String stockFeatureTopic;

    @Value("${topic.analysis.stock.features.dlt}")
    private String dltStockFeatureTopic;

    @Value("${topic.market.ticks}")
    private String marketTicksTopic;

    @Value("${topic.market.ticks.dlt}")
    private String dltMarketTicksTopic;
}
