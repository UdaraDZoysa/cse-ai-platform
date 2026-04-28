package com.harsha.market_data_service.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.market_data_service.model.TradeSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class MarketDataParser {
    private final ObjectMapper mapper;

    public MarketDataParser(ObjectMapper mapper) {

        this.mapper = mapper;
    }

    public TradeSummaryResponse parse(String rawJson) {
        try {
            return mapper.readValue(rawJson, TradeSummaryResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSE trade summary response", e);
        }
    }
}
