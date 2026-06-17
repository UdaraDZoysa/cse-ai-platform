package com.harsha.investment_intelligence_service.application.api.controller;

import com.harsha.contracts.dto.invintelligence.invinsight.InvInsightSummaryResponse;
import com.harsha.contracts.dto.invintelligence.stock.MarketInsightHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.PriceHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.StockOverviewResponse;
import com.harsha.investment_intelligence_service.application.api.service.StockQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:9191")
public class StockController {
    private final StockQueryService service;

    public StockController(
            StockQueryService service
    ) {
        this.service = service;
    }

    @GetMapping("/{symbol}/overview")
    public StockOverviewResponse getOverview(
            @PathVariable("symbol") String symbol
    ) {
        return service.getStockOverview(symbol);
    }

    @GetMapping("/{symbol}/investment-insights")
    public Page<InvInsightSummaryResponse> getInvInsights(
            @PathVariable("symbol") String symbol,
            Pageable pageable
    ) {
        return service.getInvInsights(symbol, pageable);
    }

    @GetMapping("/{symbol}/price-history")
    public PriceHistoryResponse getPriceHistory(
            @PathVariable("symbol") String symbol,
            @RequestParam(defaultValue = "30") int days
    ) {
        return service.getPriceHistory(
                symbol,
                Instant.now().minus(days, ChronoUnit.DAYS),
                Instant.now()
        );
    }

    @GetMapping("/{symbol}/market-insights")
    public Page<MarketInsightHistoryResponse> getMarketInsights(
            @PathVariable("symbol") String symbol,
            Pageable pageable
    ) {
        return service.getMarketInsightHistory(
                symbol,
                pageable
        );
    }
}
