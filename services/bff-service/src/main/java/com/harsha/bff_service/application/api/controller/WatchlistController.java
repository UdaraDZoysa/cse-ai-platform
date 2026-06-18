package com.harsha.bff_service.application.api.controller;

import com.harsha.bff_service.application.api.service.WatchlistService;
import com.harsha.contracts.dto.invintelligence.watchlist.WatchlistResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(origins = "http://localhost:3000")
public class WatchlistController {
    private final WatchlistService service;

    public WatchlistController(
            WatchlistService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> update(
            @RequestBody Set<String> symbols
    ) {
        service.updateWatchlist(symbols);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public WatchlistResponse get() {
        return service.getWatchlist();
    }
}
