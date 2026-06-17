package com.harsha.investment_intelligence_service.application.api.adapter.invinsight;

import com.harsha.contracts.dto.invintelligence.invinsight.InvInsightSummaryResponse;
import com.harsha.contracts.dto.invintelligence.invinsight.InvestmentInsightDetailResponse;
import com.harsha.investment_intelligence_service.application.api.mapper.invinsight.InvInsightMapper;
import com.harsha.investment_intelligence_service.application.api.repository.invinsight.InvInsightReadRepository;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AIReasoningJobStatus;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class InvInsightReadRepositoryAdapter implements InvInsightReadRepository {
    private final AiReasoningJobRepository repository;
    private final InvInsightMapper mapper;

    public InvInsightReadRepositoryAdapter(
            AiReasoningJobRepository repository,
            InvInsightMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Page<InvInsightSummaryResponse> findInsights(
            Pageable pageable
    ) {
        return repository
                .findByStatusOrderByCreatedAtDesc(
                        AIReasoningJobStatus.PROCESSED,
                        pageable
                )
                .map(mapper::toSummaryResponse);
    }

    @Override
    public InvestmentInsightDetailResponse findInsight(
            String id
    ) {
        return repository
                .findById(id)
                .map(mapper::toDetailedResponse)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Insight not found"
                        )
                );
    }
}
