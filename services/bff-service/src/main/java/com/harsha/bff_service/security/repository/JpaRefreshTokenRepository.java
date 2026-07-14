package com.harsha.bff_service.security.repository;

import com.harsha.bff_service.security.entity.RefreshToken;
import com.harsha.bff_service.security.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRefreshTokenRepository 
        extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(UserEntity user);
}
