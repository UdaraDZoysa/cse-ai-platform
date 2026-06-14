package com.harsha.investment_intelligence_service.application.reasoning.prompt;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AIReasoningContext;
import com.harsha.investment_intelligence_service.domain.model.reasoning.ReasoningPromptRequest;
import com.harsha.investment_intelligence_service.util.NumberFormatter;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {
    public ReasoningPromptRequest build(
            AIReasoningContext context
    ) {

        MarketSnapshotEvent market =
                context.marketSnapshot();

        StringBuilder prompt =
                new StringBuilder();

        prompt.append("""
                You are an experienced investment analyst.

                Analyze the following stock opportunity.

                ========================
                MARKET SNAPSHOT
                ========================
                """);

        prompt.append(
                """
                Symbol: %s
                Company: %s
                Price: LKR %.2f
                Percentage Change: %.2f%%
                Previous Close: LKR %.2f
                Share Volume: %d
                Market Cap: LKR %s
                Open: LKR %.2f
                High: LKR %.2f
                Low: LKR %.2f
                Trade Volume: %d
                Turnover: LKR %s

                """
                        .formatted(
                                market.symbol(),
                                context.companyName(),
                                market.price(),
                                market.percentageChange(),
                                market.previousClose(),
                                market.shareVolume(),
                                NumberFormatter.humanReadable(market.marketCap()),
                                market.open(),
                                market.high(),
                                market.low(),
                                market.tradeVolume(),
                                NumberFormatter.humanReadable(market.turnover())
                        )
        );

        if (context.strategySummary() != null) {

            prompt.append("""
                    ========================
                    STRATEGY SUMMARY
                    ========================
                    """);

            prompt.append(
                    """
                    Current Strategy Confidence Score: %.0f / 100
                    Average Strategy Confidence Score: %.0f / 100
                    Strategy Confidence Trend: %.2f
                    Strategy Confidence Volatility: %.2f
                    Persistence: %d
                    Status: %s
                    Regime: %s

                    """
                            .formatted(
                                    context.strategySummary()
                                            .currentConfidence() * 100,

                                    context.strategySummary()
                                            .averageConfidence() * 100,

                                    context.strategySummary()
                                            .confidenceTrend(),

                                    context.strategySummary()
                                            .confidenceVolatility(),

                                    context.strategySummary()
                                            .persistence(),

                                    context.strategySummary()
                                            .status(),

                                    context.strategySummary()
                                            .regime()
                            )
            );

            prompt.append(
                    """
        
                    Strategy Status Interpretation:
            
                    STRENGTHENED
                    - Strong positive signal
            
                    OPENED
                    - Positive signal
            
                    WEAKENED
                    - Opportunity remains active
                    - Conviction has weakened
                    - Cautionary signal
            
                    INVALIDATED
                    - Opportunity is no longer actionable
                    - Strong negative signal
                    - Narrative insights alone should not override this signal
            
                    """
            );
        }

        appendStrategyHistory(
                prompt,
                context
        );

        if (context.transitionSummary() != null) {

            prompt.append("""
                    ========================
                    TRANSITIONS
                    ========================
                    """);

            prompt.append(
                    """
                    Transition Count: %d
                    Bullish Transitions: %d
                    Bearish Transitions: %d
                    Reversals: %d
                    Confidence Increases: %d
                    Confidence Decreases: %d

                    """
                            .formatted(
                                    context.transitionSummary()
                                            .transitionCount(),

                                    context.transitionSummary()
                                            .bullishTransitions(),

                                    context.transitionSummary()
                                            .bearishTransitions(),

                                    context.transitionSummary()
                                            .reversals(),

                                    context.transitionSummary()
                                            .confidenceIncreases(),

                                    context.transitionSummary()
                                            .confidenceDecreases()
                            )
            );

            prompt.append(
                    """
        
                    Transition Interpretation:
            
                    Bullish Transitions
                    - Improving opportunity quality
            
                    Bearish Transitions
                    - Deteriorating opportunity quality
            
                    Confidence Increases
                    - Increasing conviction
            
                    Confidence Decreases
                    - Weakening conviction
            
                    Reversals
                    - Indicates instability
            
                    """
            );
        }

        appendTransitionHistory(
                prompt,
                context
        );

        if (!context.activeInsights().isEmpty()) {

            prompt.append("""
                    ========================
                    ACTIVE INSIGHTS
                    ========================
                    """);

            for (MarketInsightGeneratedEvent insight
                    : context.activeInsights()) {

                prompt.append(
                        """
                        Summary: %s
                        Narrative Sentiment: %s
                        Importance Score: %.2f
                        Persistence Score: %.2f
                        Narrative Confidence Score: %.2f
                        Reason: %s
                        
                        """
                                .formatted(
                                        insight.summary(),
                                        insight.sentiment(),
                                        insight.importanceScore(),
                                        insight.persistenceScore(),
                                        insight.confidenceScore(),
                                        insight.reasoning()
                                )
                );
            }
        }

        prompt.append(
                """
        
                Insight Score Interpretation:
        
                Importance Score
                - Measures potential impact
        
                Persistence Score
                - Measures expected durability
        
                Narrative Confidence Score
                - Measures confidence in the narrative assessment
        
                """
        );

        prompt.append("""
        ========================
        SIGNAL WEIGHTING GUIDANCE
        ========================

        Current Market Signals:
        - Market Snapshot
        - Strategy Summary
        - Transition Summary

        These should be weighted more heavily when determining:

        - Market Assessment
        - Recommended Action
        - Risk Level
        - Expected Market Behavior
        - Confidence Score

        Narrative Signals:
        - Active Insights

        These should be weighted more heavily when determining:

        - Medium-term outlook
        - Long-term outlook
        - Supporting Factors

        When signals conflict:

        - For INTRA_DAY and SHORT_TERM assessments, prioritize current strategy signals.
        - For MEDIUM_TERM and LONG_TERM assessments, narrative signals may partially offset weak strategy signals.
        - Narrative signals alone must not override:
          - INVALIDATED status
          - Very low confidence
          - Multiple bearish transitions
          - Persistent confidence deterioration
          
        - Persistent confidence deterioration requires explicit evidence of declining confidence.
        - Do not treat stable low confidence as confidence deterioration.

        ========================
        CONFIDENCE SCORING GUIDANCE
        ========================

        Higher confidence generally requires:

        - High strategy confidence
        - Positive confidence trend
        - Stable regime
        - Multiple supporting signals

        Lower confidence generally results from:

        - INVALIDATED status
        - Low strategy confidence
        - Sideways regime
        - Conflicting signals
        - Bearish transitions
        - Confidence deterioration
        
        Confidence Deterioration Interpretation:
                
        - Confidence deterioration requires explicit evidence of declining confidence.
        - Use Confidence Decreases and Confidence Trend as the primary indicators.
        - Stable low confidence is NOT confidence deterioration.
        - Do not infer confidence deterioration solely because confidence is low.

        Do not assign very high confidence scores when strategy confidence is low or strategy status is INVALIDATED.
        Confidence Score Rules:
       
        The Current Strategy Confidence Score is an important input produced by the strategy engine.

        The final confidence.score should represent the overall conviction after considering:
        
         - Strategy Summary
         - Transition Summary
         - Strategy History
         - Transition History
         - Active Insights
         - Market Snapshot
                
         The final confidence.score may be higher or lower than the Current Strategy Confidence Score when additional evidence supports that conclusion.
                
         Explain clearly which factors increased or decreased the final confidence.
       
        Do not significantly increase confidence based solely on narrative signals when Current Strategy Confidence Score is very low unless multiple independent signals support the same conclusion.

        When Current Strategy Confidence Score is 0:
       
        - Do not increase confidence based solely on narrative sentiment.
        - Only assign a value above 0 when multiple independent signals support the same conclusion, such as:
          - Positive Confidence Trend
          - Bullish Transitions exceed Bearish Transitions
          - Strong supporting insights

        Current Strategy Confidence Score is produced by the strategy engine and represents the overall strength of the detected opportunity after combining multiple market signals.
        Treat it as an important indicator of opportunity quality, but consider all available evidence before determining the final confidence score.

        ========================
        ACTION CONSISTENCY RULES
        ========================

        Recommended Action should generally align with Market Assessment.
        
        The action should not be more aggressive than the market assessment.
                
        Examples:
                
        STRONG_BULLISH -> STRONG_BUY or BUY
        BULLISH -> BUY or ACCUMULATE
        NEUTRAL -> HOLD
        BEARISH -> REDUCE
        STRONG_BEARISH -> SELL
        
        ==========================
        INVALIDATED INTERPRETATION
        ==========================
                    
       INVALIDATED means the strategy engine does not currently detect an actionable opportunity.
                
       INVALIDATED does NOT automatically imply the company is fundamentally weak.
                
       INVALIDATED should generally be interpreted as:
                
            - NEUTRAL market assessment when narrative signals remain positive.
            - BEARISH market assessment when additional negative signals are present.
            
       Additional negative signals typically require one or more of:
                
        - Bearish Transitions > Bullish Transitions
        - Bearish Narrative Sentiment
        - Persistent confidence deterioration
        - Expected Market Behavior = LOWER 
        
        Persistent confidence deterioration requires explicit evidence of declining confidence.
                
        Do not treat stable low confidence as confidence deterioration.
        
        with MODERATE or SIGNIFICANT magnitude
                
       Do not classify as BEARISH solely because Status = INVALIDATED.
                
            - HOLD recommendation when narrative signals remain positive.
            - REDUCE recommendation when additional negative signals are present.
                
       Use STRONG_BEARISH or SELL only when additional negative signals are present, such as:
                
            - Multiple bearish transitions
            - Persistent confidence deterioration
            - Bearish narrative sentiment
            - Strong negative market behavior signals

        """);

        prompt.append("""
                ========================

                Provide:
                
                1. Executive Summary
                   - 2-3 sentence overview.
                   - Reflect both positive and negative signals.
                   - Avoid focusing exclusively on narrative insights.
                
                2. Market Assessment
                   - STRONG_BULLISH / BULLISH / NEUTRAL / BEARISH / STRONG_BEARISH
                   - Explain the reasoning.
                
                3. Recommended Action
                   - STRONG_BUY / BUY / ACCUMULATE / HOLD / REDUCE / SELL
                   - Explain why.
                
                4. Suggested Time Horizon
                   - INTRA_DAY
                   - SHORT_TERM (1-4 weeks)
                   - MEDIUM_TERM (1-6 months)
                   - LONG_TERM (6+ months)
                
                5. Expected Market Behavior
                     - Expected Direction (HIGHER / SIDEWAYS / LOWER)
                     - Expected Magnitude (SLIGHT / MODERATE / SIGNIFICANT)
                     - Brief justification.
                
                6. Key Supporting Factors
                   - List the most important bullish factors.
                
                7. Key Risks
                   - List the most important risks or bearish factors.
                
                8. Context Limitations
                   - List important information missing from the context.
                   - Examples:
                     - No earnings information provided.
                     - No valuation information provided.
                   - Only include limitations directly observable from the provided context.
                
                9. Invalidation Conditions
                
                   - Only include explicit FUTURE conditions that would invalidate the opportunity.
                
                   - Do NOT include:
                
                     - Current Status
                     - Current Strategy Confidence Score
                     - Current Regime
                     - Current Market Assessment
                     - Current Direction
                     - Current Observations
                
                   - Examples of values that MUST NOT appear:
                
                     - INVALIDATED status
                     - Low confidence score
                     - Sideways regime
                     - Bearish transitions
                     - Current market weakness
                
                   - If the supplied context does not explicitly define future invalidation conditions,
                   - return an empty list.
                
                   - Return an empty list by default unless explicit future invalidation conditions are present.
                   
                   IMPORTANT:
                
                   Current observations are NOT invalidation conditions.
                
                   The following are never valid invalidation conditions:
                
                   - Current Status
                   - Current Strategy Confidence Score
                   - Current Regime
                   - Current Direction
                   - Current Market Assessment
                   - Current Observations
                
                   Only return invalidation conditions when the supplied context explicitly describes future events or thresholds that would invalidate the thesis.
                
                   If none exist, return [].
                
                10. Risk Level
                    - LOW / MEDIUM / HIGH
                    - Brief justification.
                
                11. Confidence Score
                    - Integer between 0 and 100.
                    - Explain the primary factors contributing to this confidence level.
                
                Base the assessment strictly on the information provided in the context.
                
                Do not use external knowledge, financial statements, earnings reports, analyst opinions, news events, sector trends, macroeconomic assumptions, or any information not explicitly present in the context.
                
                If information is insufficient to support a conclusion, explicitly state the limitation. 
                Return ONLY valid JSON with the following structure:
                
                JSON Requirements:
                
                - Return ONLY valid JSON.
                - All enum values must exactly match the allowed values.
                - Do not invent enum values.
                - Do not misspell enum values.
                - Do not include markdown.
                - Do not include explanations outside the JSON object.
                
                {
                  "executiveSummary": "",
                  "marketAssessment": {
                    "sentiment": "",
                    "reasoning": ""
                  },
                  "recommendedAction": {
                    "action": "",
                    "reasoning": ""
                  },
                  "timeHorizon": "",
                  "expectedMarketBehavior": {
                    "direction": "",
                    "magnitude": "",
                    "justification": ""
                  },
                  "supportingFactors": [],
                  "risks": [],
                  "contextLimitations": [],
                  "invalidationConditions": [],
                  "riskLevel": {
                    "level": "",
                    "justification": ""
                  },
                  "confidence": {
                    "score": 0,
                    "reasoning": ""
                  }
                }
                
                Allowed Values:
                
                marketAssessment.sentiment:
                - STRONG_BULLISH
                - BULLISH
                - NEUTRAL
                - BEARISH
                - STRONG_BEARISH
                
                recommendedAction.action:
                - STRONG_BUY
                - BUY
                - ACCUMULATE
                - HOLD
                - REDUCE
                - SELL
                
                timeHorizon:
                - INTRA_DAY
                - SHORT_TERM
                - MEDIUM_TERM
                - LONG_TERM
                
                expectedMarketBehavior.direction:
                - HIGHER
                - SIDEWAYS
                - LOWER
                
                expectedMarketBehavior.magnitude:
                - SLIGHT
                - MODERATE
                - SIGNIFICANT
                
                riskLevel.level:
                - LOW
                - MEDIUM
                - HIGH
                
                confidence.score:
                - Integer between 0 and 100.

                """);

        return new ReasoningPromptRequest(
                context.symbol(),
                prompt.toString()
        );
    }

    private void appendStrategyHistory(
            StringBuilder prompt,
            AIReasoningContext context
    ) {

        if (context.recentEvaluations() == null
                || context.recentEvaluations().isEmpty()
                || context.recentEvaluations().size() < 3) {
            return;
        }

        prompt.append("""
            IMPORTANT:
            
            - The Strategy Summary contains the authoritative current state.
            
            - Current Strategy Confidence Score in Strategy Summary is the current opportunity confidence.
                
            - Strategy History contains historical observations only and must not replace or override the current values in Strategy Summary.
                
            - When discussing current confidence, current status, current regime, or current conviction, always use values from Strategy Summary.
                
            - Use Strategy History only to identify trends, stability, persistence, and historical evolution.
                
            ========================
            STRATEGY HISTORY
            ========================
            
            Recent Strategy Evaluations:
            
            """);

        for (var evaluation : context.recentEvaluations()) {

            prompt.append(
                    """
                    Confidence: %.0f / 100
                    Status: %s
                    Regime: %s
                    Direction: %s
                    Persistence: %d
                    
                    """
                            .formatted(
                                    evaluation.confidence() * 100,
                                    evaluation.status(),
                                    evaluation.marketRegime(),
                                    evaluation.direction(),
                                    evaluation.persistence()
                            )
            );
        }

        prompt.append("""
            
            Interpretation Guidance:
            
            - Consistently increasing confidence indicates strengthening opportunity quality.
            - Consistently decreasing confidence indicates deteriorating opportunity quality.
            - Stable regimes generally indicate stronger conviction.
            - Frequent regime changes may indicate instability.
            - Increasing persistence indicates opportunity durability.
            - Recent Strategy Evaluations are historical observations only.
            - For current opportunity assessment, use the Current Strategy Confidence Score from the Strategy Summary as the authoritative current value.
            - Do not replace the Current Strategy Confidence Score with historical confidence values from Strategy History.
            
            """);
    }

    private void appendTransitionHistory(
            StringBuilder prompt,
            AIReasoningContext context
    ) {

        if (context.recentTransitions() == null
                || context.recentTransitions().isEmpty()
                || context.recentTransitions().size() < 3) {
            return;
        }

        prompt.append("""
            ========================
            RECENT TRANSITIONS
            ========================
            
            Opportunity Evolution:
            
            """);

        for (var transition : context.recentTransitions()) {

            prompt.append(
                    """
                    Previous Status: %s
                    Current Status: %s
                    Previous Strategy Confidence: %.0f /100
                    Current Strategy Confidence: %.0f /100
                    Previous Regime: %s
                    Current Regime: %s
                    Reasons: %s
                    
                    """
                            .formatted(
                                    transition.previousStatus(),
                                    transition.currentStatus(),
                                    transition.previousConfidence() * 100,
                                    transition.currentConfidence() * 100,
                                    transition.previousRegime(),
                                    transition.currentRegime(),
                                    transition.reasons()
                            )
            );
        }

        prompt.append("""
            
            Interpretation Guidance:
            
            - Bullish transitions indicate improving opportunity quality.
            - Bearish transitions indicate deteriorating opportunity quality.
            - Multiple confidence increases indicate strengthening conviction.
            - Multiple confidence decreases indicate weakening conviction.
            - Frequent reversals indicate unstable market conditions.
            
            """);
    }
}
