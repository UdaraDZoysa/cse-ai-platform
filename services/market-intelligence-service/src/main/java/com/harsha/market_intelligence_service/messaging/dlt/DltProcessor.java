package com.harsha.market_intelligence_service.messaging.dlt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DltProcessor {
    private final DltRepository dltRepository;
    private final DltMessageService dltMessageService;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(DltProcessor.class);

    public DltProcessor(
            DltRepository dltRepository,
            DltMessageService dltMessageService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.dltRepository = dltRepository;
        this.dltMessageService = dltMessageService;
        this.eventPublisher = eventPublisher;
    }

    public void process() {
        if (!processing.compareAndSet(false, true)) {
            return;
        }
        try {
            while(true) {
                List<DltMessage> messages = dltRepository.lockNextBatch();

                if (messages.isEmpty()) {
                    break;
                }

                for (DltMessage message : messages) {
                    try {
                        dltMessageService.processSingleMessage(message);
                    } catch (Exception e) {
                        log.error("Unexpected failure for Dlt message → id={}, reason={}",
                                message.getId(),
                                e.getMessage());
                    }
                }
            }

        } finally {
            processing.set(false);
            if (dltRepository.existsByPendingMessage(Instant.now()) > 0) {
                eventPublisher.publishEvent(
                        new DltProcessingRequested()
                );
            }
        }
    }
}
