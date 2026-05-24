package com.harsha.market_intelligence_service.ingestion.client;

import com.harsha.market_intelligence_service.ingestion.dto.CseApprovedAnnouncementsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CseApprovedAnnouncementClient {
    private final WebClient cseWebClient;

    public CseApprovedAnnouncementClient(
            WebClient cseWebClient
    ) {
        this.cseWebClient = cseWebClient;
    }

    public CseApprovedAnnouncementsResponse fetch() {
        return cseWebClient
                .get()
                .uri("/approvedAnnouncement")
                .retrieve()
                .bodyToMono(CseApprovedAnnouncementsResponse.class)
                .block();
    }
}
