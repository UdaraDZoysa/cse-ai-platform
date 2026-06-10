package com.harsha.analysis_service.persistence.adapter;

import com.harsha.analysis_service.domain.repository.StockTickRepository;
import com.harsha.analysis_service.persistence.mapper.StockTickMapper;
import com.harsha.analysis_service.persistence.repository.JpaStockTickRepository;
import com.harsha.contracts.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockTickRepositoryAdapter
        implements StockTickRepository {

    private final JpaStockTickRepository repository;
    private final StockTickMapper mapper;

    public StockTickRepositoryAdapter(
            JpaStockTickRepository repository,
            StockTickMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(StockTickEvent event) {
        repository.save(
                mapper.toEntity(event)
        );
    }

    @Override
    public List<StockTickEvent> findLatestBySymbol(
            String symbol,
            int limit) {

        return repository
                .findLatestTicks(symbol, limit)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<String> findTrackedSymbols() {
        return repository.findTrackedSymbols();
    }

    @Override
    public int deleteOlderThan(long cutoff) {
        return repository.deleteOlderThan(cutoff);
    }
}
