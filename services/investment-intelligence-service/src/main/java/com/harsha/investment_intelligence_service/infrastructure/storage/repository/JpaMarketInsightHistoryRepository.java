package com.harsha.investment_intelligence_service.infrastructure.storage.repository;

import com.harsha.investment_intelligence_service.infrastructure.storage.entity.MarketInsightHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaMarketInsightHistoryRepository
        extends JpaRepository<MarketInsightHistoryEntity, UUID> {

    List<MarketInsightHistoryEntity> findBySymbolOrderByOccurredAtDesc(String symbol);

    Page<MarketInsightHistoryEntity> findBySymbolOrderByOccurredAtDesc(
            String symbol,
            Pageable pageable
    );
}
