package com.harsha.investment_intelligence_service.application.api.controller;

import com.harsha.contracts.dto.invintelligence.watchlist.WatchlistResponse;
import com.harsha.investment_intelligence_service.application.api.service.WatchlistQueryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(origins = "http://localhost:9191")
public class WatchlistController {
    private final WatchlistQueryService service;

    public WatchlistController(
            WatchlistQueryService service
    ) {
        this.service = service;
    }

    @GetMapping
    public WatchlistResponse getWatchlist() {
        return service.getWatchlist();
    }
}