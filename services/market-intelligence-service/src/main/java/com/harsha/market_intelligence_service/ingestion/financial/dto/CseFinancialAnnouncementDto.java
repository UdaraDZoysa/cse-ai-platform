package com.harsha.market_intelligence_service.ingestion.financial.dto;

public record CseFinancialAnnouncementDto(
        Long id,
        String path,
        Long manualDate,
        String uploadedDate,
        String fileText,
        String name,
        String authorizedDate
) {
}
