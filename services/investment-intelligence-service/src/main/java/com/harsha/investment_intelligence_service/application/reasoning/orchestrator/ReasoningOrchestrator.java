package com.harsha.investment_intelligence_service.application.reasoning.orchestrator;

import com.harsha.investment_intelligence_service.application.reasoning.parser.JsonResponseCleaner;
import com.harsha.investment_intelligence_service.application.reasoning.provider.ReasoningProvider;
import com.harsha.investment_intelligence_service.application.reasoning.provider.ReasoningProviderRegistry;
import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ProviderType;
import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ReasoningRequest;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.ReasoningResponse;
import org.springframework.stereotype.Component;

@Component
public class ReasoningOrchestrator {
    private final ReasoningProviderRegistry reasoningProviderRegistry;

    public ReasoningOrchestrator(
            ReasoningProviderRegistry reasoningProviderRegistry
    ) {
        this.reasoningProviderRegistry = reasoningProviderRegistry;
    }

    public ReasoningResponse generateResponse(
            String symbol,
            String prompt,
            String model,
            ProviderType providerType
    ) {
        ReasoningProvider provider = reasoningProviderRegistry.get(
                providerType
        );

        return provider.generate(
                new ReasoningRequest(
                        symbol,
                        prompt,
                        model
                )
        );
    }
}
