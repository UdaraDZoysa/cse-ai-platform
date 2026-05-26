package com.harsha.market_intelligence_service.shared.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ToJsonParser {

    public static String toJson(Object object, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
