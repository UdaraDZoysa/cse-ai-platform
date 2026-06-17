package com.harsha.investment_intelligence_service.application.api.service;

import com.harsha.contracts.dto.invintelligence.marketinsight.MarketInsightDetailResponse;
import com.harsha.investment_intelligence_service.application.api.repository.marketinsight.MarketInsightReadRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MarketInsightQueryService {
    private final MarketInsightReadRepository repository;

    public MarketInsightQueryService(
            MarketInsightReadRepository repository
    ) {
        this.repository = repository;
    }

    public MarketInsightDetailResponse getMarketInsightDetail(UUID id) {
        return repository.findMarketInsightById(id);
    }
}
