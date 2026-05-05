package com.harsha.analysis_service.dispatcher;

import com.harsha.analysis_service.handler.EventHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventDispatcher {
    private final ObjectMapper objectMapper;
    private final Map<String, EventHandler<?>> handlers = new HashMap<>();

    public EventDispatcher(ObjectMapper objectMapper, List<EventHandler<?>> handlerList) {
        this.objectMapper = objectMapper;

        for (EventHandler<?> handler : handlerList) {
            handlers.put(handler.eventType(), handler);
        }
    }

    public void dispatch(String eventType, String payload) {
        EventHandler<?> handler = handlers.get(eventType);

        if (handler == null) {
            throw new RuntimeException("No handler for event type: " + eventType);
        }

        handler.handle(payload, objectMapper);
    }
}
