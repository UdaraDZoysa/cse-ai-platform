package com.harsha.market_intelligence_service.narrative.cache;

import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeIntelligenceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class NarrativeCacheService {
    private final NarrativeIntelligenceRepository repository;

    public NarrativeCacheService(
            NarrativeIntelligenceRepository repository) {
        this.repository = repository;
    }

    public boolean isStale(String symbol) {
        Optional<NarrativeIntelligence> optional
                = repository.findBySymbol(symbol);

        //No Searched Data
        if (optional.isEmpty()) {
            return true;
        }

        NarrativeIntelligence intelligence = optional.get();

        return intelligence.getExpiresAt().isBefore(Instant.now());
    }
}
