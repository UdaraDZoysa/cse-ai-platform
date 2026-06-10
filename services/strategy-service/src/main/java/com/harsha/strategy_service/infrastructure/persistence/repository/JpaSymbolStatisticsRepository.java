package com.harsha.strategy_service.infrastructure.persistence.repository;

import com.harsha.strategy_service.infrastructure.persistence.entity.SymbolStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSymbolStatisticsRepository
        extends JpaRepository<SymbolStatisticsEntity, String> {
}
