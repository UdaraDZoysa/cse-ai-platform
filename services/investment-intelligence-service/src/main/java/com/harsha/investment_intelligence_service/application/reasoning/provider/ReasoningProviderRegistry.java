package com.harsha.investment_intelligence_service.application.reasoning.provider;

import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ProviderType;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import com.harsha.investment_intelligence_service.exception.ai.NonRetryableAIException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReasoningProviderRegistry {
    private final Map<ProviderType, ReasoningProvider> providers;

    public ReasoningProviderRegistry(
            List<ReasoningProvider> providers
    ) {
        this.providers = providers.stream()
                .collect(
                        Collectors.toMap(
                                ReasoningProvider::providerType,
                                Function.identity()
                        )
                );
    }

    public ReasoningProvider get(
            ProviderType providerType
    ) {
        ReasoningProvider provider =
                providers.get(providerType);

        if (provider == null) {

            throw new NonRetryableAIException(
                    "Provider not found: " + providerType,
                    ProcessingErrorType.INVALID_CONFIGURATION,
                    null
            );
        }
        return provider;
    }
}
