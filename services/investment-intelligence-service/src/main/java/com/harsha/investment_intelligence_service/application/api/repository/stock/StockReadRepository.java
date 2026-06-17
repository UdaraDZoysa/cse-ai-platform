package com.harsha.investment_intelligence_service.application.api.repository.stock;

import com.harsha.contracts.dto.invintelligence.invinsight.InvInsightSummaryResponse;
import com.harsha.contracts.dto.invintelligence.stock.MarketInsightHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.PriceHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.StockOverviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockReadRepository {
    Page<InvInsightSummaryResponse> getInsights(
            String symbol,
            Pageable pageable
    );

    StockOverviewResponse findOverview(
            String symbol
    );

    PriceHistoryResponse findPriceHistory(
            String symbol,
            long from,
            long to
    );

    Page<MarketInsightHistoryResponse> findMarketInsightHistory(
            String symbol,
            Pageable pageable
    );
}
