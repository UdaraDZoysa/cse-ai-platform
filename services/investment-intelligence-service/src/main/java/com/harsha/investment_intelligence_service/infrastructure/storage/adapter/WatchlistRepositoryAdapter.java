package com.harsha.investment_intelligence_service.infrastructure.storage.adapter;

import com.harsha.contracts.events.common.WatchlistUpdatedEvent;
import com.harsha.investment_intelligence_service.domain.repository.WatchlistRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.mapper.WatchlistMapper;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaWatchlistRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WatchlistRepositoryAdapter
        implements WatchlistRepository {

    private final JpaWatchlistRepository repository;
    private final WatchlistMapper mapper;

    public WatchlistRepositoryAdapter(
            JpaWatchlistRepository repository,
            WatchlistMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(WatchlistUpdatedEvent event) {
        repository.save(
                mapper.toEntity(event)
        );
    }

    @Override
    public Optional<WatchlistUpdatedEvent> findLatest() {
        return repository
                .findTopByOrderByUpdatedAtDesc()
                .map(mapper::toDomain);
    }
}
