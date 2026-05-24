package com.harsha.market_intelligence_service.ingestion.dto;

public record CseApprovedAnnouncementDto(
        Long id,
        String announcementCategory,
        String company,
        String remarks,
        String dateOfAnnouncement
) {
}
