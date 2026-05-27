package com.harsha.market_intelligence_service.masterdata.service;

import com.harsha.market_intelligence_service.masterdata.client.CseAllSecurityCodeClient;
import com.harsha.market_intelligence_service.masterdata.dto.CseAllSecurityCodeDto;
import com.harsha.market_intelligence_service.masterdata.util.CompanyNameNormalizer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanySymbolResolver {
    private final CseAllSecurityCodeClient client;
    private final Map<String, String> companyToSymbolMappings = new HashMap<>();
    private final Map<String, String> symbolToCompanyMappings = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(CompanySymbolResolver.class);

    public CompanySymbolResolver(
            CseAllSecurityCodeClient client) {
        this.client = client;
    }

    @PostConstruct
    public void load() {
        List<CseAllSecurityCodeDto> securityCodes = client.fetch();

        if (securityCodes == null || securityCodes.isEmpty()) {
            return;
        }

        for (CseAllSecurityCodeDto securityCode : securityCodes) {
            if (securityCode.name() == null
                    || securityCode.symbol() == null) {
                continue;
            }

            String normalizedName = CompanyNameNormalizer.normalize(
                    securityCode.name()
            );

            companyToSymbolMappings.put(normalizedName, securityCode.symbol());
            symbolToCompanyMappings.put(securityCode.symbol(), securityCode.name());
        }
        log.info(
                "Loaded {} company-symbol mappings",
                companyToSymbolMappings.size()
        );
    }

    public String resolveSymbol(String companyName) {
        String normalizedName = CompanyNameNormalizer.normalize(
                companyName
        );

        return companyToSymbolMappings.get(normalizedName);
    }

    public String resolveCompanyName(String symbol) {
        return symbolToCompanyMappings.get(symbol);
    }
}
