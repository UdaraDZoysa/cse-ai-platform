package com.harsha.investment_intelligence_service.domain.repository;

import com.harsha.contracts.events.common.WatchlistUpdatedEvent;

import java.util.Optional;

public interface WatchlistRepository {
    void save(
            WatchlistUpdatedEvent event
    );

    Optional<WatchlistUpdatedEvent> findLatest();
}
