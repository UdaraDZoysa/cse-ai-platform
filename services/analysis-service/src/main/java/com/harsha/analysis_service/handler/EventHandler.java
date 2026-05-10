package com.harsha.analysis_service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.analysis_service.exception.InvalidEventException;
import com.harsha.analysis_service.exception.NonRetryableProcessingException;
import com.harsha.analysis_service.exception.RetryableProcessingException;
import com.harsha.analysis_service.inbox.InboxEvent;

public interface EventHandler<T> {
    String eventType();

    Class<T> eventClass();

    void handle(
            String eventId,
            T event
    );

    default void handle(InboxEvent inboxEvent, ObjectMapper objectMapper) {
        try {
            T event = objectMapper.readValue(inboxEvent.getPayload(), eventClass());
            handle(inboxEvent.getId(), event);
        } catch (JsonProcessingException e) {
            throw new InvalidEventException(e);
        } catch (RuntimeException e) {
            if (e instanceof InvalidEventException ||
                    e instanceof RetryableProcessingException ||
                    e instanceof NonRetryableProcessingException) {
                throw e;
            }
            throw new RetryableProcessingException(e);
        } catch (Exception e) {
            throw new NonRetryableProcessingException(e);
        }
    }
}
