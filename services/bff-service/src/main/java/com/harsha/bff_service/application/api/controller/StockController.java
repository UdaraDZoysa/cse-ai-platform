package com.harsha.bff_service.application.api.controller;

import com.harsha.bff_service.application.api.service.StockClientService;
import com.harsha.contracts.dto.invintelligence.stock.PriceHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.StockOverviewResponse;
import com.harsha.contracts.dto.marketintelligence.NarrativeIntelligenceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:3000")
public class StockController {
    private final StockClientService service;

    public StockController(
            StockClientService service
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
    public ResponseEntity<String> getInvInsights(
            @PathVariable("symbol") String symbol,
            Pageable pageable
    ) {
        return service.getInvInsights(
                symbol,
                pageable
        );
    }

    @GetMapping("/{symbol}/price-history")
    public PriceHistoryResponse getPriceHistory(
            @PathVariable("symbol") String symbol,
            @RequestParam(defaultValue = "30") int days
    ) {
        return service.getPriceHistory(
                symbol,
                days
        );
    }

    @GetMapping("/{symbol}/market-insights")
    public ResponseEntity<String> getMarketInsights(
            @PathVariable("symbol") String symbol,
            Pageable pageable
    ) {
        return service.getMarketInsights(
                symbol,
                pageable
        );
    }

    @GetMapping("/{symbol}/market-narrative")
    public NarrativeIntelligenceResponse getMarketNarratives(
            @PathVariable("symbol") String symbol
    ) {
        return service.getNarrativeIntelligence(
                symbol
        );
    }
}
