package com.harsha.market_intelligence_service.ingestion.financial.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CseFinancialAnnouncementResponse(
        @JsonProperty("reqFinancialAnnouncemnets")
        List<CseFinancialAnnouncementDto> financialAnnouncements
) {
}
