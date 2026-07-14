package com.harsha.bff_service.auth.service;

import com.harsha.bff_service.auth.entity.RefreshTokenEntity;
import com.harsha.bff_service.auth.exception.InvalidRefreshTokenException;
import com.harsha.bff_service.auth.repository.JpaRefreshTokenRepository;
import com.harsha.bff_service.security.entity.UserEntity;
import com.harsha.bff_service.security.jwt.JwtProperties;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

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

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(user)
                .token(KeyGenerators.string().generateKey())
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

    @Transactional
    public void revokeRefreshToken(
            RefreshTokenEntity refreshToken
    ) {
        jpaRefreshTokenRepository.delete(refreshToken);
    }

    @Transactional
    public void revokeRefreshTokensByUser(
            UserEntity user
    ) {
        jpaRefreshTokenRepository.deleteByUser(user);
    }
}
