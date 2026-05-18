package com.harsha.strategy_service.application.service.idempotency;

import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {
    private final ProcessedEventRepository repository;

    public IdempotencyService(ProcessedEventRepository repository) {
        this.repository = repository;
    }

    public boolean alreadyProcessed(String eventId) {
        return repository.existsById(eventId);
    }

    public void markProcessed(String eventId) {
        repository.save(new ProcessedEvent(eventId));
    }
}
