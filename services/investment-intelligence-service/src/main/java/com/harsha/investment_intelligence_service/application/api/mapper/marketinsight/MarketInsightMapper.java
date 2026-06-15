package com.harsha.investment_intelligence_service.application.api.mapper.marketinsight;

import com.harsha.contracts.dto.marketinsight.MarketInsightDetailResponse;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.MarketInsightHistoryEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MarketInsightMapper {
    public MarketInsightDetailResponse toDetailResponse(
            MarketInsightHistoryEntity entity
    ) {
        return new MarketInsightDetailResponse(
                entity.getId().toString(),
                entity.getSymbol(),
                entity.getCompany(),
                entity.getSummary(),
                entity.getReasoning(),
                entity.getSentiment().name(),
                entity.getImportanceScore(),
                entity.getConfidenceScore(),
                entity.getPersistenceScore(),
                entity.getGeneratedBy(),
                Instant.ofEpochMilli(entity.getOccurredAt())
        );
    }
}
