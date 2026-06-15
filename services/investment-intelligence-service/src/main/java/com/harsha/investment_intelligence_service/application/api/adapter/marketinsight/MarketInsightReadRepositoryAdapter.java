package com.harsha.investment_intelligence_service.application.api.adapter.marketinsight;

import com.harsha.contracts.dto.marketinsight.MarketInsightDetailResponse;
import com.harsha.investment_intelligence_service.application.api.mapper.marketinsight.MarketInsightMapper;
import com.harsha.investment_intelligence_service.application.api.repository.marketinsight.MarketInsightReadRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaMarketInsightHistoryRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MarketInsightReadRepositoryAdapter
        implements MarketInsightReadRepository {
    private final JpaMarketInsightHistoryRepository repository;
    private final MarketInsightMapper mapper;

    public MarketInsightReadRepositoryAdapter(
            JpaMarketInsightHistoryRepository repository,
            MarketInsightMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public MarketInsightDetailResponse findMarketInsightById(UUID id) {
        return repository
                .findById(id)
                .map(mapper::toDetailResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Market Insight Not Found"
                        )
                );
    }
}
