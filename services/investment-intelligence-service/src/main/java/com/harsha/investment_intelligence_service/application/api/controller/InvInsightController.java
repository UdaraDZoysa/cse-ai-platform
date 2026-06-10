package com.harsha.investment_intelligence_service.application.api.controller;

import com.harsha.investment_intelligence_service.application.api.dto.InvInsightSummaryResponse;
import com.harsha.investment_intelligence_service.application.api.service.InvInsightQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/investment-insights")
@CrossOrigin(origins = "http://localhost:3000")
public class InvInsightController {
    private final InvInsightQueryService service;

    public InvInsightController(
            InvInsightQueryService service
    ) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public Page<InvInsightSummaryResponse> getInvInsights(
            Pageable pageable
    ) {
        return service.getInvInsights(pageable);
    }
}
