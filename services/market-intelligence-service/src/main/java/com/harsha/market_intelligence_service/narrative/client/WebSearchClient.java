package com.harsha.market_intelligence_service.narrative.client;

import com.harsha.market_intelligence_service.narrative.dto.NarrativeExtractionResult;
import com.harsha.market_intelligence_service.narrative.dto.WebSearchResult;

import java.util.List;

public interface WebSearchClient {
    NarrativeExtractionResult search(
            String symbol,
            String companyName
    );
}
