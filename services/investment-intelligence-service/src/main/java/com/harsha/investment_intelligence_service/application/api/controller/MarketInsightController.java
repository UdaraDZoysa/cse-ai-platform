package com.harsha.investment_intelligence_service.application.api.controller;

import com.harsha.contracts.dto.invintelligence.marketinsight.MarketInsightDetailResponse;
import com.harsha.investment_intelligence_service.application.api.service.MarketInsightQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/market-insight")
@CrossOrigin(origins = "http://localhost:9191")
public class MarketInsightController {
    private final MarketInsightQueryService service;

    public MarketInsightController(
            MarketInsightQueryService service
    ) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public MarketInsightDetailResponse getMarketInsightDetails(
            @PathVariable("id") UUID id
    ) {
        return service.getMarketInsightDetail(id);
    }
}