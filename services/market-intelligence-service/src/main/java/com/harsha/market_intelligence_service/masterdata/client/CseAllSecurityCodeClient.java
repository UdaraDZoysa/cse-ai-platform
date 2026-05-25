package com.harsha.market_intelligence_service.masterdata.client;

import com.harsha.market_intelligence_service.masterdata.dto.CseAllSecurityCodeDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class CseAllSecurityCodeClient {
    private final WebClient webClient;

    public CseAllSecurityCodeClient(
            WebClient webClient) {
        this.webClient = webClient;
    }

    public List<CseAllSecurityCodeDto> fetch() {
        return webClient.get()
                .uri("https://www.cse.lk/api/allSecurityCode")
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<
                                List<CseAllSecurityCodeDto>>() {
                        }
                )
                .block();
    }
}
