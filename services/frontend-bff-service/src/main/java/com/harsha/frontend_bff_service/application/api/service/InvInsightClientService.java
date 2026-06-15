package com.harsha.frontend_bff_service.application.api.service;

import com.harsha.contracts.dto.invinsight.InvestmentInsightDetailResponse;
import com.harsha.frontend_bff_service.application.api.client.InvestmentIntelligenceClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InvInsightClientService {
    private final InvestmentIntelligenceClient investmentIntelligenceClient;

    public InvInsightClientService(
            InvestmentIntelligenceClient investmentIntelligenceClient
    ) {
        this.investmentIntelligenceClient = investmentIntelligenceClient;
    }

    public ResponseEntity<String> getInvInsights(
            Pageable pageable
    ) {
        return investmentIntelligenceClient.getInvInsights(pageable);
    }

    public InvestmentInsightDetailResponse getInvInsight(
            String id
    ) {
        return investmentIntelligenceClient.getInvInsight(id);
    }
}
