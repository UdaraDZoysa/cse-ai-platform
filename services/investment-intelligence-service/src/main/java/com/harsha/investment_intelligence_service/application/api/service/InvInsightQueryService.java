package com.harsha.investment_intelligence_service.application.api.service;

import com.harsha.contracts.dto.invintelligence.invinsight.InvInsightSummaryResponse;
import com.harsha.contracts.dto.invintelligence.invinsight.InvestmentInsightDetailResponse;
import com.harsha.investment_intelligence_service.application.api.repository.invinsight.InvInsightReadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InvInsightQueryService {
    private final InvInsightReadRepository readRepository;


    public InvInsightQueryService(
            InvInsightReadRepository readRepository
    ) {
        this.readRepository = readRepository;
    }

    public Page<InvInsightSummaryResponse> getInvInsights(
            Pageable pageable
    ) {
        return readRepository.findInsights(pageable);
    }

    public InvestmentInsightDetailResponse getDetailedInvInsight(
            String id
    ) {
        return readRepository.findInsight(id);
    }

}
