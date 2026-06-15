package com.harsha.frontend_bff_service.application.api.service;

import com.harsha.contracts.dto.marketinsight.MarketInsightDetailResponse;
import com.harsha.frontend_bff_service.application.api.client.InvestmentIntelligenceClient;
import org.springframework.stereotype.Service;

@Service
public class MarketInsightClientService {
    private final InvestmentIntelligenceClient investmentIntelligenceClient;

    public MarketInsightClientService(
            InvestmentIntelligenceClient investmentIntelligenceClient
    ) {
        this.investmentIntelligenceClient = investmentIntelligenceClient;
    }

    public MarketInsightDetailResponse getMarketInsight(
            String id
    ) {
        return investmentIntelligenceClient.getMarketInsight(id);
    }
}
