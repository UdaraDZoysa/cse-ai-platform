package com.harsha.investment_intelligence_service.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.exception.NonRetryableProcessingException;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.handler.EventHandler;
import com.harsha.investment_intelligence_service.messaging.inbox.InboxEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventDispatcher {
    private final ObjectMapper objectMapper;
    private final Map<EventType, EventHandler<?>> handlers = new HashMap<>();

    public EventDispatcher(
            ObjectMapper objectMapper,
            List<EventHandler<?>> handlerList
    ) {
        this.objectMapper = objectMapper;

        for (EventHandler<?> handler : handlerList) {
            handlers.put(handler.eventType(), handler);
        }
    }

    public void dispatch(InboxEvent inboxEvent) {
        EventHandler<?> handler = handlers.get(inboxEvent.getEventType());

        if (handler == null) {
            throw new NonRetryableProcessingException(
                    "No handler registered for event type: "
                            + inboxEvent.getEventType(),
                    ProcessingErrorType.HANDLER_NOT_FOUND,
                    null
            );
        }
        handler.handle(inboxEvent, objectMapper);
    }
}
