package com.harsha.investment_intelligence_service.application.api.controller;

import com.harsha.contracts.dto.invinsight.InvInsightSummaryResponse;
import com.harsha.contracts.dto.invinsight.InvestmentInsightDetailResponse;
import com.harsha.investment_intelligence_service.application.api.service.InvInsightQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investment-insights")
@CrossOrigin(origins = "http://localhost:9191")
public class InvInsightController {
    private final InvInsightQueryService service;

    public InvInsightController(
            InvInsightQueryService service
    ) {
        this.service = service;
    }

    @GetMapping
    public Page<InvInsightSummaryResponse> getInvInsights(
            Pageable pageable
    ) {
        return service.getInvInsights(pageable);
    }

    @GetMapping("/{id}")
    public InvestmentInsightDetailResponse getInvInsight(
            @PathVariable String id
    ) {
        return service.getDetailedInvInsight(id);
    }
}
