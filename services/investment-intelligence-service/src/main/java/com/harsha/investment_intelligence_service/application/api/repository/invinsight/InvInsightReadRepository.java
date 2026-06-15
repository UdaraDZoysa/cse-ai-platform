package com.harsha.investment_intelligence_service.application.api.repository.invinsight;

import com.harsha.contracts.dto.invinsight.InvInsightSummaryResponse;
import com.harsha.contracts.dto.invinsight.InvestmentInsightDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvInsightReadRepository {
    Page<InvInsightSummaryResponse> findInsights(
            Pageable pageable
    );

    InvestmentInsightDetailResponse findInsight(
            String id
    );
}
