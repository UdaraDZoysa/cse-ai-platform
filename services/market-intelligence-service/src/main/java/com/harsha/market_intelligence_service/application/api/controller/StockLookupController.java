package com.harsha.market_intelligence_service.application.api.controller;

import com.harsha.contracts.dto.marketintelligence.StockLookupResponse;
import com.harsha.market_intelligence_service.application.api.service.StockLookupService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock-lookup")
@CrossOrigin(origins = "http://localhost:9191")
public class StockLookupController {
    private final StockLookupService service;

    public StockLookupController(
            StockLookupService service
    ) {
        this.service = service;
    }

    @GetMapping
    public StockLookupResponse getStock() {
        return service.getAllStocks();
    }
}
