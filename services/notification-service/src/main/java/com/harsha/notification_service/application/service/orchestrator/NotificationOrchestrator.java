package com.harsha.notification_service.application.service.orchestrator;

import com.harsha.contracts.events.investment_intelligence.InvestmentInsightGeneratedEvent;
import com.harsha.notification_service.application.service.detector.InsightChangeDetector;
import com.harsha.notification_service.application.service.evaluator.NotificationEvaluator;
import com.harsha.notification_service.application.service.formatter.NotificationFormatter;
import com.harsha.notification_service.application.service.notification.NotificationJobPersistenceService;
import com.harsha.notification_service.domain.entity.InsightState;
import com.harsha.notification_service.domain.model.InsightChange;
import com.harsha.notification_service.domain.model.NotificationChannel;
import com.harsha.notification_service.domain.model.NotificationDecision;
import com.harsha.notification_service.domain.model.NotificationMessage;
import com.harsha.notification_service.domain.repository.InsightStateRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationOrchestrator {
    private final InsightStateRepository repository;
    private final InsightChangeDetector detector;
    private final NotificationEvaluator evaluator;
    private final NotificationFormatter formatter;
    private final NotificationJobPersistenceService jobPersistenceService;

    public NotificationOrchestrator(
            InsightStateRepository repository,
            InsightChangeDetector detector,
            NotificationEvaluator evaluator,
            NotificationFormatter formatter,
            NotificationJobPersistenceService jobPersistenceService
    ) {
        this.repository = repository;
        this.detector = detector;
        this.evaluator = evaluator;
        this.formatter = formatter;
        this.jobPersistenceService = jobPersistenceService;
    }

    @Transactional
    public void process(
            InvestmentInsightGeneratedEvent event
    ) {

        InsightState previous = repository
                .findById(event.symbol())
                .orElse(null);

        InsightChange change = detector.detect(
                previous,
                event
        );

        NotificationDecision decision = evaluator.evaluate(change);

        NotificationMessage message = formatter.format(
                event,
                decision.priority()
        );

        jobPersistenceService.persistJob(
                message,
                //Hardcoded for now
                NotificationChannel.TELEGRAM
        );

        if (previous == null) {
            repository.save(InsightState.from(event));

        } else {
            previous.update(event);
            repository.save(previous);
        }
    }
}
