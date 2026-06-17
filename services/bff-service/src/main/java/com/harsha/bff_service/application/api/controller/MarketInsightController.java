package com.harsha.bff_service.application.api.controller;

import com.harsha.bff_service.application.api.service.MarketInsightClientService;
import com.harsha.contracts.dto.invintelligence.marketinsight.MarketInsightDetailResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market-insight")
@CrossOrigin(origins = "http://localhost:3000")
public class MarketInsightController {
    private final MarketInsightClientService service;

    public MarketInsightController(
            MarketInsightClientService service
    ) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public MarketInsightDetailResponse getMarketInsight(
            @PathVariable("id") String id
    ) {
        return service.getMarketInsight(id);
    }
}
