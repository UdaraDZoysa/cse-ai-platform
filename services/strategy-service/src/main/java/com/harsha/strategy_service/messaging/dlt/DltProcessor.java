package com.harsha.strategy_service.messaging.dlt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DltProcessor {
   private final DltRepository dltRepository;
   private final DltMessageService dltMessagePublisher;

    private static final Logger log = LoggerFactory.getLogger(DltProcessor.class);

    public DltProcessor(
            DltRepository dltRepository,
            DltMessageService dltMessagePublisher
    ) {
        this.dltRepository = dltRepository;
        this.dltMessagePublisher = dltMessagePublisher;
    }

    @Scheduled(fixedRate = 5000)
    public void process() {
        List<DltMessage> messages = dltRepository.lockNextBatch();

        for (DltMessage message : messages) {
            try {
                dltMessagePublisher.processSingleMessage(message);
            } catch (Exception e) {
                log.error("Unexpected failure for Dlt message → id={}, reason={}",
                        message.getId(),
                        e.getMessage());
            }
        }
    }
}
