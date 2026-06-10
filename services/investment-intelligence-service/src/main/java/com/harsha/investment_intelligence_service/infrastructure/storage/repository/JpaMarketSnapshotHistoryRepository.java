package com.harsha.investment_intelligence_service.infrastructure.storage.repository;

import com.harsha.investment_intelligence_service.infrastructure.storage.entity.MarketSnapshotHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaMarketSnapshotHistoryRepository
        extends JpaRepository<MarketSnapshotHistoryEntity, UUID> {

    Optional<MarketSnapshotHistoryEntity> findTopBySymbolOrderByOccurredAtDesc(
            String symbol
    );
}
