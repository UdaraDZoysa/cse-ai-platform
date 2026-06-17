package com.harsha.bff_service.application.api.service;

import com.harsha.bff_service.application.api.client.MarketIntelligenceClient;
import com.harsha.contracts.dto.marketintelligence.MarketNarrativeDetailsResponse;
import org.springframework.stereotype.Service;

@Service
public class NarrativeSourceClientService {
    private final MarketIntelligenceClient marketIntelligenceClient;

    public NarrativeSourceClientService(
            MarketIntelligenceClient marketIntelligenceClient
    ) {
        this.marketIntelligenceClient = marketIntelligenceClient;
    }

    public MarketNarrativeDetailsResponse getNarrativeDetails(
            String id
    ) {
        return marketIntelligenceClient.getNarrativeDetails(
                id
        );
    }
}
