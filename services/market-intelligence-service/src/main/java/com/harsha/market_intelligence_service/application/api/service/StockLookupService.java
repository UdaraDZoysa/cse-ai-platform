package com.harsha.market_intelligence_service.application.api.service;

import com.harsha.contracts.dto.marketintelligence.StockLookupResponse;
import com.harsha.market_intelligence_service.masterdata.service.CompanySymbolResolver;
import org.springframework.stereotype.Service;

@Service
public class StockLookupService {
    private final CompanySymbolResolver companySymbolResolver;

    public StockLookupService(
            CompanySymbolResolver companySymbolResolver
    ) {
        this.companySymbolResolver = companySymbolResolver;
    }

    public StockLookupResponse getAllStocks() {
        return new StockLookupResponse(
                companySymbolResolver.getAllStocks()
        );
    }
}
