package com.harsha.market_intelligence_service.ingestion.announcement.service;

import com.harsha.market_intelligence_service.ingestion.announcement.client.CseApprovedAnnouncementClient;
import com.harsha.market_intelligence_service.ingestion.announcement.dto.CseApprovedAnnouncementDto;
import com.harsha.market_intelligence_service.ingestion.announcement.mapper.RawMarketEventMapperAnnouncement;
import com.harsha.market_intelligence_service.masterdata.service.CompanySymbolResolver;
import com.harsha.market_intelligence_service.memory.entity.RawMarketEvent;
import com.harsha.market_intelligence_service.memory.repository.RawMarketEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ApprovedAnnouncementIngestionService {
    private final CseApprovedAnnouncementClient client;
    private final RawMarketEventMapperAnnouncement mapper;
    private final RawMarketEventRepository repository;
    private final CompanySymbolResolver symbolResolver;
    private static final Logger log = LoggerFactory.getLogger(ApprovedAnnouncementIngestionService.class);

    public ApprovedAnnouncementIngestionService(
            CseApprovedAnnouncementClient client,
            RawMarketEventMapperAnnouncement mapper,
            RawMarketEventRepository repository,
            CompanySymbolResolver symbolResolver
    ) {
        this.client = client;
        this.mapper = mapper;
        this.repository = repository;
        this.symbolResolver = symbolResolver;
    }

    public void ingest(Set<String> targetSymbols) {
        var response = client.fetch();

        if (response == null || response.approvedAnnouncements() == null) {
            return;
        }

        for (CseApprovedAnnouncementDto dto :
                response.approvedAnnouncements()) {

            String symbol = symbolResolver.resolve(dto.company());
            if (!targetSymbols.contains(symbol)) {
                log.info(
                        "Skipping irrelevant company: {}"+ " :From Approved Announcements",
                        dto.company()
                );
                continue;
            }

            try {
                RawMarketEvent event =
                        mapper.fromApprovedAnnouncement(
                                dto,
                                symbol
                        );

                repository.save(event);

                log.info(
                        "Saved approved announcement: {}",
                        symbol
                );
            } catch (DataIntegrityViolationException ex) {
                log.debug(
                        "Duplicate Approved Announcement Event skipped: {}",
                        dto.id()
                );
            }
        }
    }
}
