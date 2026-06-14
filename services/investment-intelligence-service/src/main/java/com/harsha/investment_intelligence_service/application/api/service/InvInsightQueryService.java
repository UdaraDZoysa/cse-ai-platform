package com.harsha.investment_intelligence_service.application.api.service;

import com.harsha.investment_intelligence_service.application.api.dto.invinsight.InvInsightSummaryResponse;
import com.harsha.investment_intelligence_service.application.api.dto.invinsight.InvestmentInsightDetailResponse;
import com.harsha.investment_intelligence_service.application.api.mapper.invinsight.InvInsightMapper;
import com.harsha.investment_intelligence_service.application.api.repository.invinsight.InvInsightReadRepository;
import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AIReasoningJobStatus;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
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
