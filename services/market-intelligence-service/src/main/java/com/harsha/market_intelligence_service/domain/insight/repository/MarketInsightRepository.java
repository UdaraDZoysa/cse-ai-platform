package com.harsha.market_intelligence_service.domain.insight.repository;

import com.harsha.market_intelligence_service.domain.insight.entity.MarketInsight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketInsightRepository
        extends JpaRepository<MarketInsight, Long> {

    Optional<MarketInsight> findBySymbol(String symbol);
}
