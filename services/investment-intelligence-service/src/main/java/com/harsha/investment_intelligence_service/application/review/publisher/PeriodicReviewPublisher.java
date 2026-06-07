package com.harsha.investment_intelligence_service.application.review.publisher;

import com.harsha.contracts.events.investment_intelligence.InvestmentInsightGeneratedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.events.EventPublisher;
import com.harsha.investment_intelligence_service.application.insight.InvestmentInsightMapper;
import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.ReviewType;
import com.harsha.investment_intelligence_service.domain.model.reasoning.response.dto.InvestmentReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PeriodicReviewPublisher implements ReviewPublisher{
    private final InvestmentInsightMapper mapper;
    private final EventPublisher eventPublisher;

    private static final Logger log = LoggerFactory.getLogger(PeriodicReviewPublisher.class);

    public PeriodicReviewPublisher(
            InvestmentInsightMapper mapper,
            EventPublisher eventPublisher
    ) {
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ReviewType supportedType() {
        return ReviewType.PERIODIC_REVIEW;
    }

    @Override
    public void publish(
            InvestmentReview review,
            AiReasoningJob job
    ) {
        InvestmentInsightGeneratedEvent event =
                mapper.map(
                        review,
                        job.getSymbol(),
                        job.getProviderType(),
                        job.getModel()
                );

        log.info(
                "Publishing review. symbol={}, reviewType={}",
                job.getSymbol(),
                job.getReviewType()
        );

        eventPublisher.publish(
                job.getSymbol(),
                EventType.INVESTMENT_INSIGHT_GENERATED_EVENT,
                event
        );
    }
}
