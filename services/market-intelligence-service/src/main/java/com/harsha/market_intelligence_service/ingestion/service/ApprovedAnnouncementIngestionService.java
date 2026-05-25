package com.harsha.market_intelligence_service.ingestion.service;

import com.harsha.market_intelligence_service.filtering.service.MarketEventFilterService;
import com.harsha.market_intelligence_service.ingestion.client.CseApprovedAnnouncementClient;
import com.harsha.market_intelligence_service.ingestion.dto.CseApprovedAnnouncementDto;
import com.harsha.market_intelligence_service.ingestion.dto.SourceType;
import com.harsha.market_intelligence_service.ingestion.mapper.RawMarketEventMapper;
import com.harsha.market_intelligence_service.masterdata.service.CompanySymbolResolver;
import com.harsha.market_intelligence_service.memory.entity.RawMarketEvent;
import com.harsha.market_intelligence_service.memory.repository.RawMarketEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApprovedAnnouncementIngestionService {
    private final CseApprovedAnnouncementClient client;
    private final RawMarketEventMapper mapper;
    private final RawMarketEventRepository repository;
    private final CompanySymbolResolver symbolResolver;
    private final MarketEventFilterService filterService;
    private static final Logger log = LoggerFactory.getLogger(ApprovedAnnouncementIngestionService.class);

    public ApprovedAnnouncementIngestionService(
            CseApprovedAnnouncementClient client,
            RawMarketEventMapper mapper,
            RawMarketEventRepository repository,
            CompanySymbolResolver symbolResolver,
            MarketEventFilterService filterService
    ) {
        this.client = client;
        this.mapper = mapper;
        this.repository = repository;
        this.symbolResolver = symbolResolver;
        this.filterService = filterService;
    }

    public void ingest() {
        if (!filterService.isReady()) {

            log.info(
                    "Skipping ingestion because watchlist not initialized"
            );

            return;
        }

        var response = client.fetch();

        if (response == null || response.approvedAnnouncements() == null) {
            return;
        }

        for (CseApprovedAnnouncementDto dto :
                response.approvedAnnouncements()) {

            String symbol = symbolResolver.resolve(dto.company());
            if (!filterService.isRelevant(symbol)) {
                log.info(
                        "Skipping irrelevant company: {}",
                        dto.company()
                );
                continue;
            }

            boolean exists =
                    repository.existsByExternalIdAndSourceType(
                            dto.id().toString(),
                            SourceType.APPROVED_ANNOUNCEMENT.name()
                    );

            if (exists) {
                continue;
            }

            RawMarketEvent event =
                    mapper.fromApprovedAnnouncement(dto);

            repository.save(event);

            log.info(
                    "Saved approved announcement: {}",
                    event.getCompany()
            );
        }
    }
}
