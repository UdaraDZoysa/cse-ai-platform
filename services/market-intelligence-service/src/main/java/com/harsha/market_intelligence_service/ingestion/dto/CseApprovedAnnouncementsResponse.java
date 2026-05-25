package com.harsha.market_intelligence_service.ingestion.dto;

import java.util.List;

public record CseApprovedAnnouncementsResponse(
        List<CseApprovedAnnouncementDto> approvedAnnouncements
) {
}
