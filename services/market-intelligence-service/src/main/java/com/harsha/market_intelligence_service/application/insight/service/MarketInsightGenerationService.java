package com.harsha.market_intelligence_service.application.insight.service;

import com.harsha.market_intelligence_service.application.insight.mapper.MarketInsightMapper;
import com.harsha.market_intelligence_service.application.insight.port.NarrativeSynthesisClient;
import com.harsha.market_intelligence_service.application.insight.prompt.MarketInsightPromptBuilder;
import com.harsha.market_intelligence_service.domain.insight.entity.MarketInsight;
import com.harsha.market_intelligence_service.domain.insight.model.InsightGenerationContext;
import com.harsha.market_intelligence_service.domain.insight.model.MarketInsightResult;
import org.springframework.stereotype.Service;

@Service
public class MarketInsightGenerationService {
    private final MarketInsightPromptBuilder promptBuilder;
    private final NarrativeSynthesisClient synthesisClient;
    private final MarketInsightMapper mapper;

    public MarketInsightGenerationService(
            MarketInsightPromptBuilder promptBuilder,
            NarrativeSynthesisClient synthesisClient,
            MarketInsightMapper mapper) {
        this.promptBuilder = promptBuilder;
        this.synthesisClient = synthesisClient;
        this.mapper = mapper;
    }

    public MarketInsight generate(
            InsightGenerationContext context
    ) {
        String prompt =
                promptBuilder.build(context);

        MarketInsightResult result =
                synthesisClient.synthesize(prompt);

        return mapper.map(
                context,
                result
        );
    }
}
