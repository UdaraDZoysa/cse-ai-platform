package com.harsha.investment_intelligence_service.domain.repository;

import com.harsha.contracts.events.market.MarketSnapshotEvent;

import java.util.Optional;

public interface MarketSnapshotHistoryRepository {
    void save(MarketSnapshotEvent event);

    Optional<MarketSnapshotEvent> findLatest(
            String symbol
    );
}
