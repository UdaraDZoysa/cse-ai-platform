package com.harsha.market_intelligence_service.memory.repository;

import com.harsha.market_intelligence_service.memory.entity.RawMarketEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMarketEventRepository
        extends JpaRepository<RawMarketEvent, Long> {

    boolean existsByExternalIdAndSourceType(
            String externalId,
            String sourceType
    );
}
