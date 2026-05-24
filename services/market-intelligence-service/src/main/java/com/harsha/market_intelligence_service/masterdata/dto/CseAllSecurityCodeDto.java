package com.harsha.market_intelligence_service.masterdata.dto;

public record CseAllSecurityCodeDto(
        Integer id,
        String name,
        String symbol,
        Integer active
) {
}
