package com.harsha.investment_intelligence_service.application.api.repository.marketinsight;

import com.harsha.contracts.dto.marketinsight.MarketInsightDetailResponse;

import java.util.UUID;

public interface MarketInsightReadRepository {
    MarketInsightDetailResponse findMarketInsightById(
            UUID id
    );
}
