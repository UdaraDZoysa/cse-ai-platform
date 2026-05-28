package com.harsha.market_intelligence_service.narrative.repositoryy;

import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NarrativeIntelligenceRepository extends JpaRepository<NarrativeIntelligence, Long> {
    Optional<NarrativeIntelligence> findBySymbol(String symbol);

    @Query("""
            SELECT DISTINCT ni
            FROM NarrativeIntelligence ni
            LEFT JOIN FETCH ni.sources
            WHERE ni.symbol = :symbol
            """)
    Optional<NarrativeIntelligence> findBySymbolWithSources(
            @Param("symbol") String symbol
    );
}
