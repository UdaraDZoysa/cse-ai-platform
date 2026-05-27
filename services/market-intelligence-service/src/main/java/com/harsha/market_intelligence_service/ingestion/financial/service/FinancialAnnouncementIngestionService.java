package com.harsha.market_intelligence_service.ingestion.financial.service;

import com.harsha.market_intelligence_service.ingestion.financial.client.CseFinancialAnnouncementClient;
import com.harsha.market_intelligence_service.ingestion.financial.dto.CseFinancialAnnouncementDto;
import com.harsha.market_intelligence_service.ingestion.financial.mapper.RawMarketEventMapperFinancial;
import com.harsha.market_intelligence_service.masterdata.service.CompanySymbolResolver;
import com.harsha.market_intelligence_service.memory.entity.RawMarketEvent;
import com.harsha.market_intelligence_service.memory.repository.RawMarketEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FinancialAnnouncementIngestionService {
    private final CseFinancialAnnouncementClient client;
    private final RawMarketEventMapperFinancial mapper;
    private final RawMarketEventRepository repository;
    private final CompanySymbolResolver symbolResolver;
    private static final Logger log = LoggerFactory.getLogger(FinancialAnnouncementIngestionService.class);

    public FinancialAnnouncementIngestionService(
            CseFinancialAnnouncementClient client,
            RawMarketEventMapperFinancial mapper,
            RawMarketEventRepository repository,
            CompanySymbolResolver symbolResolver) {
        this.client = client;
        this.mapper = mapper;
        this.repository = repository;
        this.symbolResolver = symbolResolver;
    }

    public void ingest(Set<String> targetSymbols) {
        var response = client.fetch();

        if (response == null || response.financialAnnouncements() == null) {
            return;
        }

        for (CseFinancialAnnouncementDto dto :
                response.financialAnnouncements()) {
            String symbol = symbolResolver.resolveSymbol(dto.name());

            if (!targetSymbols.contains(symbol)) {
                log.info(
                        "Skipping irrelevant company: {}" + " :From Financial Announcements",
                        dto.name()
                );
                continue;
            }

            try {

                RawMarketEvent event =
                        mapper.fromFinancialAnnouncement(
                                dto,
                                symbol
                        );

                repository.save(event);

                log.info(
                        "Saved financial announcement: {}",
                        symbol
                );

            } catch (DataIntegrityViolationException ex) {

                log.debug(
                        "Duplicate Financial Announcement Event skipped: {}",
                        dto.id()
                );
            }
        }
    }
}
