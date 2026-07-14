package com.harsha.bff_service.auth.repository;

import com.harsha.bff_service.auth.entity.RefreshTokenEntity;
import com.harsha.bff_service.security.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRefreshTokenRepository 
        extends JpaRepository<RefreshTokenEntity, Long> {
    
    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByUser(UserEntity user);
}
