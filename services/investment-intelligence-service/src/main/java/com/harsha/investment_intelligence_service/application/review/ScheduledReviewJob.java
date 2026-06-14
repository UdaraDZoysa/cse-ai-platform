package com.harsha.investment_intelligence_service.application.review;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ScheduledReviewJob {
    private final SymbolReviewService reviewService;

    public ScheduledReviewJob(
            SymbolReviewService reviewService
    ) {
        this.reviewService = reviewService;
    }

    @Scheduled(
            fixedDelay = 3,
            timeUnit = TimeUnit.MINUTES
    )
    public void run() {
        reviewService.reviewAllSymbols();
    }
}
