package com.harsha.investment_intelligence_service.infrastructure.storage.repository;

import com.harsha.investment_intelligence_service.application.api.dto.stock.PriceHistoryProjection;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.MarketSnapshotHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaMarketSnapshotHistoryRepository
        extends JpaRepository<MarketSnapshotHistoryEntity, UUID> {

    Optional<MarketSnapshotHistoryEntity> findTopBySymbolOrderByOccurredAtDesc(
            String symbol
    );

    @Query("""
       select new com.harsha.investment_intelligence_service.application.api.dto.stock.PriceHistoryProjection(
           m.occurredAt,
           m.price
       )
       from MarketSnapshotHistoryEntity m
       where m.symbol = :symbol
             and m.occurredAt between :from and :to
       order by m.occurredAt
       """)
    List<PriceHistoryProjection> findPriceHistory(
            String symbol,
            long from,
            long to
    );
}