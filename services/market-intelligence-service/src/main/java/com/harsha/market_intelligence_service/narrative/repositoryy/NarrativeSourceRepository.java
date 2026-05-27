package com.harsha.market_intelligence_service.narrative.repositoryy;

import com.harsha.market_intelligence_service.narrative.entity.NarrativeSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NarrativeSourceRepository
        extends JpaRepository<NarrativeSource, Long> {
    boolean existsBySourceUrl(String sourceUrl);
}
