package com.harsha.bff_service.application.api.controller;

import com.harsha.bff_service.application.api.service.InvInsightClientService;
import com.harsha.contracts.dto.invintelligence.invinsight.InvestmentInsightDetailResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investment-insights")
@CrossOrigin(origins = "http://localhost:3000")
public class InvInsightController {
    private final InvInsightClientService service;

    public InvInsightController(
            InvInsightClientService service
    ) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<String> getInvInsights(
            Pageable pageable
    ) {
        return service.getInvInsights(pageable);
    }

    @GetMapping("/{id}")
    public InvestmentInsightDetailResponse getInvInsight(
            @PathVariable("id") String id
    ) {
        return service.getInvInsight(id);
    }
}
