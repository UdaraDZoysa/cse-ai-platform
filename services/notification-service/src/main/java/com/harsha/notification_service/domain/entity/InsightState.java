package com.harsha.notification_service.domain.entity;

import com.harsha.contracts.events.investment_intelligence.InvestmentInsightGeneratedEvent;
import com.harsha.contracts.events.investment_intelligence.enums.MarketSentiment;
import com.harsha.contracts.events.investment_intelligence.enums.RecommendedAction;
import com.harsha.contracts.events.investment_intelligence.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "insight_state")
@Getter
public class InsightState {
    @Id
    private String symbol;

    @Enumerated(EnumType.STRING)
    private RecommendedAction action;

    @Enumerated(EnumType.STRING)
    private MarketSentiment sentiment;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    private int confidenceScore;

    private Instant updatedAt;

    protected InsightState() {
    }

    public static InsightState from(
            InvestmentInsightGeneratedEvent event
    ) {
        InsightState state = new InsightState();

        state.symbol = event.symbol();
        state.action = event.action();
        state.sentiment = event.sentiment();
        state.riskLevel = event.riskLevel();
        state.confidenceScore = event.confidenceScore();
        state.updatedAt = Instant.now();

        return state;
    }

    public void update(
            InvestmentInsightGeneratedEvent event
    ) {
        this.action = event.action();
        this.sentiment = event.sentiment();
        this.riskLevel = event.riskLevel();
        this.confidenceScore = event.confidenceScore();
        this.updatedAt = Instant.now();
    }
}
