package com.harsha.market_intelligence_service.application.insight.port;

import com.harsha.market_intelligence_service.domain.insight.model.MarketInsightResult;

public interface NarrativeSynthesisClient {

    MarketInsightResult synthesize(
            String prompt
    );
}
