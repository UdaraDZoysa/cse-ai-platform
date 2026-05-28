package com.harsha.market_intelligence_service.application.insight.service;

import com.harsha.market_intelligence_service.domain.insight.entity.MarketInsight;
import com.harsha.market_intelligence_service.domain.insight.repository.MarketInsightRepository;
import org.springframework.stereotype.Service;

@Service
public class MarketInsightPersistenceService {
    private final MarketInsightRepository repository;

    public MarketInsightPersistenceService(MarketInsightRepository repository) {
        this.repository = repository;
    }

    public MarketInsight save(
            MarketInsight insight
    ) {
        return repository.save(insight);
    }
}
