package com.harsha.market_intelligence_service.ingestion.financial.client;

import com.harsha.market_intelligence_service.ingestion.financial.dto.CseFinancialAnnouncementResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CseFinancialAnnouncementClient {
    private final WebClient cseWebClient;

    public CseFinancialAnnouncementClient(
            WebClient cseWebClient) {
        this.cseWebClient = cseWebClient;
    }

    public CseFinancialAnnouncementResponse fetch() {
        return cseWebClient
                .post()
                .uri("/getFinancialAnnouncement")
                .retrieve()
                .bodyToMono(CseFinancialAnnouncementResponse.class)
                .block();
    }
}
