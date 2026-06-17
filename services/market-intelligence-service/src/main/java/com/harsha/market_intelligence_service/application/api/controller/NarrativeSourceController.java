package com.harsha.market_intelligence_service.application.api.controller;

import com.harsha.contracts.dto.marketintelligence.MarketNarrativeDetailsResponse;
import com.harsha.contracts.dto.marketintelligence.NarrativeIntelligenceResponse;
import com.harsha.market_intelligence_service.application.api.service.NarrativeSourceQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market-narrative")
public class NarrativeSourceController {
    private final NarrativeSourceQueryService service;

    public NarrativeSourceController(
            NarrativeSourceQueryService service
    ) {
        this.service = service;
    }

    @GetMapping("/{symbol}")
    public NarrativeIntelligenceResponse getNarrativeIntelligence(
            @PathVariable("symbol") String symbol
    ) {
        return service.getNarrativeIntelligence(symbol);
    }

    @GetMapping("/details/{id}")
    public MarketNarrativeDetailsResponse getNarrativeDetails(
            @PathVariable("id") Long id
    ) {
        return service.getMarketNarrativeDetails(id);
    }
}
