package com.harsha.investment_intelligence_service.domain.repository;

import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.investment_intelligence_service.domain.model.history.OpportunityTransitionHistory;

import java.util.List;

public interface OpportunityTransitionHistoryRepository {

    void save(OpportunityTransitionEvent event);

    List<OpportunityTransitionEvent> findLatest(
            String symbol,
            int limit
    );
}
