package com.harsha.frontend_bff_service.application.api.controller;

import com.harsha.contracts.dto.marketinsight.MarketInsightDetailResponse;
import com.harsha.frontend_bff_service.application.api.service.MarketInsightClientService;
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
