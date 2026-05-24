package com.harsha.market_intelligence_service.ingestion.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.market_intelligence_service.ingestion.dto.CseApprovedAnnouncementDto;
import com.harsha.market_intelligence_service.ingestion.dto.SourceType;
import com.harsha.market_intelligence_service.masterdata.service.CompanySymbolResolver;
import com.harsha.market_intelligence_service.memory.entity.RawMarketEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RawMarketEventMapper {
    private final ObjectMapper objectMapper;
    private final CompanySymbolResolver symbolResolver;

    public RawMarketEventMapper(
            ObjectMapper objectMapper,
            CompanySymbolResolver symbolResolver) {
        this.objectMapper = objectMapper;
        this.symbolResolver = symbolResolver;
    }

    public RawMarketEvent fromApprovedAnnouncement(
            CseApprovedAnnouncementDto dto
    ) {
        String symbol = symbolResolver.resolve(dto.company());

        return RawMarketEvent.builder()
                .externalId(dto.id().toString())
                .sourceType(SourceType.APPROVED_ANNOUNCEMENT.name())
                .title(dto.announcementCategory())
                .content(dto.remarks())
                .company(dto.company())
                .symbol(symbol)
                .category(dto.announcementCategory())
                .rawPayload(toJson(dto))
                .createdAt(Instant.now())
                .build();
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
