package com.harsha.market_intelligence_service.masterdata.util;

public class CompanyNameNormalizer {
    private CompanyNameNormalizer() {}

    public static String normalize(String companyName) {
        if (companyName == null || companyName.isEmpty()) {
            return null;
        }

        return companyName.toUpperCase()
                .replace("&", "AND")
                .replace(".", "")
                .replace(",", "")
                .trim();
    }
}
