package com.harsha.analysis_service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface EventHandler<T> {
    String eventType();

    Class<T> eventClass();

    void handle(T event);

    default void handle(String payload, ObjectMapper objectMapper) {
        try {
            T event = objectMapper.readValue(payload, eventClass());
            handle(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize event", e);
        }
    }
}
