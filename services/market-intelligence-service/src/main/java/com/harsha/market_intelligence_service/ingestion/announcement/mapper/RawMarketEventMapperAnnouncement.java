package com.harsha.market_intelligence_service.ingestion.announcement.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.market_intelligence_service.ingestion.announcement.dto.CseApprovedAnnouncementDto;
import com.harsha.market_intelligence_service.ingestion.shared.SourceType;
import com.harsha.market_intelligence_service.memory.entity.RawMarketEvent;
import com.harsha.market_intelligence_service.shared.util.ToJsonParser;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RawMarketEventMapperAnnouncement {
    private final ObjectMapper objectMapper;

    public RawMarketEventMapperAnnouncement(
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RawMarketEvent fromApprovedAnnouncement(
            CseApprovedAnnouncementDto dto,
            String symbol
    ) {
        return RawMarketEvent.builder()
                .externalId(dto.id().toString())
                .sourceType(SourceType.APPROVED_ANNOUNCEMENT.name())
                .title(dto.announcementCategory())
                .content(dto.remarks())
                .company(dto.company())
                .symbol(symbol)
                .category(dto.announcementCategory())
                .rawPayload(ToJsonParser.toJson(dto, objectMapper))
                .createdAt(Instant.now())
                .build();
    }
}
