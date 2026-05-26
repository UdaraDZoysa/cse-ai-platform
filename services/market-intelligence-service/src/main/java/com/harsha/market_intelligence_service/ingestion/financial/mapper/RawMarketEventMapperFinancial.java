package com.harsha.market_intelligence_service.ingestion.financial.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.market_intelligence_service.ingestion.financial.dto.CseFinancialAnnouncementDto;
import com.harsha.market_intelligence_service.ingestion.shared.SourceType;
import com.harsha.market_intelligence_service.memory.entity.RawMarketEvent;
import com.harsha.market_intelligence_service.shared.util.TimeParser;
import com.harsha.market_intelligence_service.shared.util.ToJsonParser;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RawMarketEventMapperFinancial {
    private final ObjectMapper objectMapper;

    public RawMarketEventMapperFinancial(
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RawMarketEvent fromFinancialAnnouncement(
            CseFinancialAnnouncementDto dto,
            String symbol
    ) {
        return RawMarketEvent.builder()
                .externalId(dto.id().toString())
                .sourceType(SourceType.FINANCIAL_ANNOUNCEMENT.name())
                .title(dto.fileText())
                .category(SourceType.FINANCIAL_ANNOUNCEMENT.name() )
                .content(dto.path())
                .company(dto.name())
                .symbol(symbol)
                .publishedAt(TimeParser.parseToInstant(dto.authorizedDate()))
                .rawPayload(ToJsonParser.toJson(dto, objectMapper))
                .createdAt(Instant.now())
                .build();
    }
}
