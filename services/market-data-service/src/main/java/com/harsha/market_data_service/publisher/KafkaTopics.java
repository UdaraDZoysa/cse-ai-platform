package com.harsha.market_data_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopics {
    @Value("${topic.market.ticks}")
    private String stockTicksTopic;

    public String getStockTicksTopic() {
        return stockTicksTopic;
    }
}
