package com.harsha.market_intelligence_service.application.insight.service;

import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.domain.insight.repository.InsightGenJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InsightGenJobPersistenceService {
    private final InsightGenJobRepository insightGenJobRepository;
    private static final Logger log = LoggerFactory.getLogger(InsightGenJobPersistenceService.class);

    public InsightGenJobPersistenceService(
            InsightGenJobRepository insightGenJobRepository) {
        this.insightGenJobRepository = insightGenJobRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public InsightGenerationJob persistInsightGenJob(
            String symbol
    ) {
        InsightGenerationJob result = null;
        InsightGenerationJob job =
                InsightGenerationJob.builder()
                        .symbol(symbol)
                        .build();

        try {
            result = insightGenJobRepository.save(job);

            log.debug(
                    "Insight generation job created. symbol={}",
                    symbol
            );

        } catch (Exception ex) {
            log.error(
                    "Failed to persist insight generation job. symbol={}",
                    symbol,
                    ex
            );

            throw ex;
        }

        return result;
    }
}
