package com.harsha.analysis_service.application.service.persistence;

import com.harsha.analysis_service.persistence.entity.StockFeatureSnapshotEntity;
import com.harsha.analysis_service.persistence.repository.StockFeatureSnapshotRepository;
import org.springframework.stereotype.Service;

@Service
public class FeatureSnapshotService {
    private final StockFeatureSnapshotRepository repository;

    public FeatureSnapshotService(
            StockFeatureSnapshotRepository repository
    ) {
        this.repository = repository;
    }

    public StockFeatureSnapshotEntity save (
            StockFeatureSnapshotEntity entity
    ) {
        return repository.save(entity);
    }
}
