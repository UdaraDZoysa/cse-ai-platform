package com.harsha.investment_intelligence_service.infrastructure.storage.repository;

import com.harsha.investment_intelligence_service.infrastructure.storage.entity.WatchlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaWatchlistRepository
        extends JpaRepository<WatchlistEntity, String> {

    Optional<WatchlistEntity> findTopByOrderByUpdatedAtDesc();
}
