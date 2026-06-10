package com.harsha.investment_intelligence_service.infrastructure.storage.adapter;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.investment_intelligence_service.domain.repository.MarketSnapshotHistoryRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.mapper.MarketSnapshotHistoryMapper;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaMarketSnapshotHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MarketSnapshotHistoryRepositoryAdapter
        implements MarketSnapshotHistoryRepository {

    private final JpaMarketSnapshotHistoryRepository repository;
    private final MarketSnapshotHistoryMapper mapper;

    public MarketSnapshotHistoryRepositoryAdapter(
            JpaMarketSnapshotHistoryRepository repository,
            MarketSnapshotHistoryMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(MarketSnapshotEvent event) {
        repository.save(mapper.toEntity(event));
    }

    @Override
    public Optional<MarketSnapshotEvent> findLatest(String symbol) {
        return repository
                .findTopBySymbolOrderByOccurredAtDesc(symbol)
                .map(mapper::toDomain);
    }
}
