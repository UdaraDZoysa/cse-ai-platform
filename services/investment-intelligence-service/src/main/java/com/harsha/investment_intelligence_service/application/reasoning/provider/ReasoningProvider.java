package com.harsha.investment_intelligence_service.application.reasoning.provider;

import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ProviderType;
import com.harsha.investment_intelligence_service.domain.model.reasoning.provider.ReasoningRequest;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.ReasoningResponse;

public interface ReasoningProvider {
    ProviderType providerType();

    ReasoningResponse generate(
            ReasoningRequest request
    );
}
