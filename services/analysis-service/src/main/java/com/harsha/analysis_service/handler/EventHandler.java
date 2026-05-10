package com.harsha.analysis_service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.analysis_service.exception.InvalidEventException;
import com.harsha.analysis_service.exception.NonRetryableProcessingException;
import com.harsha.analysis_service.exception.RetryableProcessingException;

public interface EventHandler<T> {
    String eventType();

    Class<T> eventClass();

    void handle(T event);

    default void handle(String payload, ObjectMapper objectMapper) {
        try {
            T event = objectMapper.readValue(payload, eventClass());
            handle(event);
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
