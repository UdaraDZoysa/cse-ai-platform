package com.harsha.bff_service.auth.service;

import com.harsha.bff_service.auth.entity.RefreshTokenEntity;
import com.harsha.bff_service.auth.exception.InvalidRefreshTokenException;
import com.harsha.bff_service.auth.repository.JpaRefreshTokenRepository;
import com.harsha.bff_service.security.entity.UserEntity;
import com.harsha.bff_service.security.jwt.JwtProperties;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(
            JpaRefreshTokenRepository jpaRefreshTokenRepository,
            JwtProperties jwtProperties
    ) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(
                                jwtProperties.getRefreshTokenExpiration()
                        )
                )
                .build();

        return jpaRefreshTokenRepository.save(refreshTokenEntity);
    }

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token);
    }

    public RefreshTokenEntity verifyExpiration(
            RefreshTokenEntity refreshToken
    ) {
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            jpaRefreshTokenRepository.delete(refreshToken);

            throw new InvalidRefreshTokenException(
                    "Refresh token has expired."
            );
        }
        return refreshToken;
    }

    public void deleteRefreshToken(
            RefreshTokenEntity refreshToken
    ) {
        jpaRefreshTokenRepository.delete(refreshToken);
    }

    public void deleteRefreshTokenByUser(
            UserEntity user
    ) {
        jpaRefreshTokenRepository.deleteByUser(user);
    }
}
