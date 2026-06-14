package com.harsha.market_intelligence_service.application.insight.service;

import com.harsha.market_intelligence_service.domain.insight.entity.MarketInsight;
import com.harsha.market_intelligence_service.domain.insight.repository.MarketInsightRepository;
import org.springframework.stereotype.Service;

@Service
public class  MarketInsightPersistenceService {
    private final MarketInsightRepository repository;

    public MarketInsightPersistenceService(MarketInsightRepository repository) {
        this.repository = repository;
    }

    public MarketInsight save(
            MarketInsight insight
    ) {
        MarketInsight existing = repository
                .findBySymbol(insight.getSymbol())
                .orElse(null);

        if (existing == null) {
            return repository.save(insight);
        }

        System.out.println("!!!!!!!!!!!!!!!!!Company: " + insight.getCompany());

        existing.setCompany(insight.getCompany());
        existing.setSummary(insight.getSummary());
        existing.setReasoning(insight.getReasoning());
        existing.setSentiment(insight.getSentiment());
        existing.setImportanceScore(insight.getImportanceScore());
        existing.setPersistenceScore(insight.getPersistenceScore());
        existing.setConfidenceScore(insight.getConfidenceScore());
        existing.setGeneratedAt(insight.getGeneratedAt());
        existing.setExpiresAt(insight.getExpiresAt());
        existing.setGeneratedBy(insight.getGeneratedBy());

        return repository.save(existing);
    }
}
