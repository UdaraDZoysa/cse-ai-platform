package com.harsha.market_intelligence_service.narrative.repositoryy;

import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NarrativeIntelligenceRepository extends JpaRepository<NarrativeIntelligence, Long> {
    Optional<NarrativeIntelligence> findBySymbol(String symbol);
}
