package com.harsha.market_intelligence_service.narrative.service;

import com.harsha.market_intelligence_service.narrative.dto.WebSearchResult;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeSource;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeSourceRepository;
import com.harsha.market_intelligence_service.shared.util.TimeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NarrativeSourcePersistenceService {
    private final NarrativeSourceRepository sourceRepository;
    private static final Logger log = LoggerFactory.getLogger(NarrativeSourcePersistenceService.class);

    public NarrativeSourcePersistenceService(
            NarrativeSourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    public void persistSources(
            NarrativeIntelligence intelligence,
            java.util.List<WebSearchResult> sources
    ) {
        for (WebSearchResult source : sources) {

            if (source.sourceUrl() == null
                    || source.sourceUrl().isBlank()) {
                continue;
            }

            Instant publishedAt = null;
            try {
                publishedAt =
                        TimeParser.parseToInstant(
                                source.publishedDate()
                        );

            } catch (Exception ex) {
                log.debug(
                        "Failed to parse published date: {} due to {}",
                        source.publishedDate(),
                        ex.getMessage()
                );
            }

            NarrativeSource entity =
                    NarrativeSource.builder()
                            .title(source.title())
                            .content(source.content())
                            .sourceUrl(source.sourceUrl())
                            .publishedDate(publishedAt)
                            .intelligence(intelligence)
                            .build();

            try {
                sourceRepository.save(entity);
            } catch (DataIntegrityViolationException ex) {
                //Already Saved Source
                log.debug(
                        "Duplicate narrative source skipped. url={}",
                        source.sourceUrl()
                );
            }
        }
    }
}
