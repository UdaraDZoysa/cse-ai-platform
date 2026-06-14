package com.harsha.investment_intelligence_service.application.api.adapter.stock;

import com.harsha.investment_intelligence_service.application.api.dto.invinsight.InvInsightSummaryResponse;
import com.harsha.investment_intelligence_service.application.api.dto.stock.MarketInsightHistoryResponse;
import com.harsha.investment_intelligence_service.application.api.dto.stock.PriceHistoryResponse;
import com.harsha.investment_intelligence_service.application.api.dto.stock.StockOverviewResponse;
import com.harsha.investment_intelligence_service.application.api.mapper.invinsight.InvInsightMapper;
import com.harsha.investment_intelligence_service.application.api.mapper.stock.StockMapper;
import com.harsha.investment_intelligence_service.application.api.repository.stock.StockReadRepository;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AIReasoningJobStatus;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaMarketInsightHistoryRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaMarketSnapshotHistoryRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class StockReadRepositoryAdapter implements StockReadRepository {
    private final AiReasoningJobRepository invInsightRepository;
    private final InvInsightMapper invInsightMapper;
    private final JpaMarketSnapshotHistoryRepository snapshotRepository;
    private final JpaMarketInsightHistoryRepository marketInsightRepository;
    private final StockMapper stockMapper;

    public StockReadRepositoryAdapter(
            AiReasoningJobRepository invInsightRepository,
            InvInsightMapper invInsightMapper,
            JpaMarketSnapshotHistoryRepository snapshotRepository,
            JpaMarketInsightHistoryRepository marketInsightRepository,
            StockMapper stockMapper
    ) {
        this.invInsightRepository = invInsightRepository;
        this.invInsightMapper = invInsightMapper;
        this.snapshotRepository = snapshotRepository;
        this.marketInsightRepository = marketInsightRepository;
        this.stockMapper = stockMapper;
    }

    @Override
    public Page<InvInsightSummaryResponse> getInsights(String symbol, Pageable pageable) {
        return invInsightRepository
                .findBySymbolAndStatusOrderByCreatedAtDesc(
                        symbol,
                        AIReasoningJobStatus.PROCESSED,
                        pageable
                ).map(invInsightMapper::toSummaryResponse);
    }

    @Override
    public StockOverviewResponse findOverview(
            String symbol
    ) {
        return snapshotRepository
                .findTopBySymbolOrderByOccurredAtDesc(symbol)
                .map(stockMapper::toOverviewResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Snapshot not found for symbol: " + symbol
                        )
                );
    }

    @Override
    public PriceHistoryResponse findPriceHistory(
            String symbol,
            long from,
            long to
    ) {
        return new PriceHistoryResponse(
                snapshotRepository
                        .findPriceHistory(symbol,from, to)
                        .stream()
                        .map(stockMapper::toPriceHistoryPoint)
                        .toList()
        );
    }

    @Override
    public Page<MarketInsightHistoryResponse> findMarketInsightHistory(String symbol, Pageable pageable) {
        return marketInsightRepository
                .findBySymbolOrderByOccurredAtDesc(symbol, pageable)
                .map(stockMapper::toMarketInsightHistoryResponse);
    }
}
