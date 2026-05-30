package com.harsha.market_intelligence_service.application.insight.service.insightGenJob;

import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.domain.insight.repository.InsightGenJobRepository;
import com.harsha.market_intelligence_service.exception.ProcessingErrorType;
import com.harsha.market_intelligence_service.exception.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InsightGenJobPersistenceService {
    private final InsightGenJobRepository insightGenJobRepository;
    private static final Logger log = LoggerFactory.getLogger(InsightGenJobPersistenceService.class);

    public InsightGenJobPersistenceService(
            InsightGenJobRepository insightGenJobRepository) {
        this.insightGenJobRepository = insightGenJobRepository;
    }

    @Transactional
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

            throw new RetryableException(
                    "Failed to persist Insight generation job",
                    ProcessingErrorType.DATABASE_ERROR,
                    ex
            );
        }

        return result;
    }
}
