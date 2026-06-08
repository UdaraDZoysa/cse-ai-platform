package com.harsha.notification_service.domain.repository;

import com.harsha.notification_service.domain.entity.InsightState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsightStateRepository
        extends JpaRepository<InsightState, String> {
}
