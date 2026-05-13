package com.harsha.analysis_service.persistence.repository;

import com.harsha.analysis_service.persistence.entity.StockFeatureSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockFeatureSnapshotRepository
        extends JpaRepository<StockFeatureSnapshotEntity, UUID> {
}
