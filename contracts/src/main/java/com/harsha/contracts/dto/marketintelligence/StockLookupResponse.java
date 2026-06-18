package com.harsha.contracts.dto.marketintelligence;

import com.harsha.contracts.events.market_intelligence.StockLookup;

import java.util.List;

public record StockLookupResponse(
        List<StockLookup> stockLookups
) {
}
