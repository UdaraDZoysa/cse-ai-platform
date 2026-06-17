package com.harsha.bff_service.application.api.controller;

import com.harsha.bff_service.application.api.service.NarrativeSourceClientService;
import com.harsha.contracts.dto.marketintelligence.MarketNarrativeDetailsResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market-narrative")
@CrossOrigin(origins = "http://localhost:3000")
public class NarrativeSourceController {
    private final NarrativeSourceClientService service;

    public NarrativeSourceController(
            NarrativeSourceClientService service
    ) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public MarketNarrativeDetailsResponse getNarrativeSource(
            @PathVariable("id") String id
    ) {
        return service.getNarrativeDetails(id);
    }
}
