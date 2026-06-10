package com.harsha.investment_intelligence_service.infrastructure.storage.adapter;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.investment_intelligence_service.domain.repository.MarketInsightHistoryRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.mapper.MarketInsightHistoryMapper;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaMarketInsightHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarketInsightHistoryRepositoryAdapter
        implements MarketInsightHistoryRepository {

    private final JpaMarketInsightHistoryRepository repository;
    private final MarketInsightHistoryMapper mapper;

    public MarketInsightHistoryRepositoryAdapter(
            JpaMarketInsightHistoryRepository repository,
            MarketInsightHistoryMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(MarketInsightGeneratedEvent event) {
        repository.save(
                mapper.toEntity(event)
        );
    }

    @Override
    public List<MarketInsightGeneratedEvent> findMarketInsights(
            String symbol
    ) {
        return repository
                .findBySymbolOrderByOccurredAtDesc(
                        symbol
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
