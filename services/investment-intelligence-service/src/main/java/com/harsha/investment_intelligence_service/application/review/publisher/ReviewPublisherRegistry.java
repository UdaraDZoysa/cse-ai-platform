package com.harsha.investment_intelligence_service.application.review.publisher;

import com.harsha.investment_intelligence_service.domain.model.reasoning.ReviewType;
import com.harsha.investment_intelligence_service.exception.NonRetryableProcessingException;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReviewPublisherRegistry {
    private final Map<ReviewType, ReviewPublisher> publishers;

    public ReviewPublisherRegistry(
            List<ReviewPublisher> publishers
    ) {
        this.publishers = publishers.stream()
                .collect(
                        Collectors.toMap(
                                ReviewPublisher::supportedType,
                                Function.identity(),
                                (a, b) -> {
                                    throw new NonRetryableProcessingException(
                                            "Duplicate ReviewPublisher for "
                                                    + a.supportedType(),
                                            ProcessingErrorType.NON_RETRYABLE,
                                            null
                                    );
                                }
                        )
                );
    }

    public ReviewPublisher get(
            ReviewType reviewType
    ) {
        ReviewPublisher publisher =
                publishers.get(reviewType);

        if (publisher == null) {
            throw new NonRetryableProcessingException(
                    "No ReviewPublisher found for reviewType="
                            + reviewType,
                    ProcessingErrorType.PUBLISHER_NOT_FOUND,
                    null
            );
        }

        return publisher;
    }
}
