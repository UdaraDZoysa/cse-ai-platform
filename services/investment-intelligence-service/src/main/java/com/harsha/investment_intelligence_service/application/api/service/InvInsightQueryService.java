package com.harsha.investment_intelligence_service.application.api.service;

import com.harsha.investment_intelligence_service.application.api.dto.InvInsightSummaryResponse;
import com.harsha.investment_intelligence_service.application.api.mapper.InvInsightMapper;
import com.harsha.investment_intelligence_service.domain.model.reasoning.AIReasoningJobStatus;
import com.harsha.investment_intelligence_service.domain.repository.AiReasoningJobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InvInsightQueryService {
    private final AiReasoningJobRepository repository;
    private final InvInsightMapper mapper;


    public InvInsightQueryService(
            AiReasoningJobRepository repository,
            InvInsightMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<InvInsightSummaryResponse> getInvInsights(
            Pageable pageable
    ) {
        return repository.findByStatusOrderByCreatedAtDesc(
                AIReasoningJobStatus.PROCESSED,
                pageable
        ).map(
                mapper::toResponse
        );
    }
}
