package com.harsha.strategy_service.messaging.dlt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class DltRecoveryScheduler {
    private final DltRepository dltRepository;
    private static final Logger log = LoggerFactory.getLogger(DltRecoveryScheduler.class);

    public DltRecoveryScheduler(DltRepository dltRepository) {
        this.dltRepository = dltRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void recoverStuckEvents() {
        Instant cutoff = Instant.now().minusSeconds(300);

        List<DltMessage> stuckMessages =
                dltRepository.findStuckProcessingEvents(cutoff);

        for (DltMessage message : stuckMessages) {
            log.debug(
                    """
                    
                    Recovering stuck event.
                    
                    id={}
                    symbol={}
                    processingStartedAt={}
                    
                    """,
                    message.getId(),
                    message.getAggregateId(),
                    message.getUpdatedAt()
            );
            message.setNextAttemptAt(Instant.now());
            message.markRetryScheduled();
            dltRepository.save(message);
        }
    }
}
