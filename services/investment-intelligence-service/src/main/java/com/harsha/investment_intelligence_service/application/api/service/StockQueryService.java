package com.harsha.investment_intelligence_service.application.api.service;

import com.harsha.contracts.dto.invintelligence.invinsight.InvInsightSummaryResponse;
import com.harsha.contracts.dto.invintelligence.stock.MarketInsightHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.PriceHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.StockOverviewResponse;
import com.harsha.investment_intelligence_service.application.api.repository.stock.StockReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StockQueryService {
    private final StockReadRepository snapshotRepository;

    public StockQueryService(
            StockReadRepository snapshotRepository
    ) {
        this.snapshotRepository = snapshotRepository;
    }

    public Page<InvInsightSummaryResponse> getInvInsights(
            String symbol,
            Pageable pageable
    ) {
        return snapshotRepository.getInsights(symbol, pageable);
    }

    public StockOverviewResponse getStockOverview(
            String symbol
    ) {
        return snapshotRepository.findOverview(symbol);
    }

    public PriceHistoryResponse getPriceHistory(
            String symbol,
            Instant from,
            Instant to
    ) {
        return snapshotRepository.findPriceHistory(
                symbol,
                from.toEpochMilli(),
                to.toEpochMilli()
        );
    }

    public Page<MarketInsightHistoryResponse> getMarketInsightHistory(
            String symbol,
            Pageable pageable
    ) {
        return snapshotRepository.findMarketInsightHistory(
                symbol,
                pageable
        );
    }
}
