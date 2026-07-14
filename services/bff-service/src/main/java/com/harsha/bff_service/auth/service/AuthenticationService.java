package com.harsha.bff_service.auth.service;

import com.harsha.bff_service.auth.dto.LoginRequest;
import com.harsha.bff_service.auth.dto.LoginResponse;
import com.harsha.bff_service.auth.dto.LogoutRequest;
import com.harsha.bff_service.auth.dto.RefreshTokenRequest;
import com.harsha.bff_service.auth.entity.RefreshTokenEntity;
import com.harsha.bff_service.auth.exception.InvalidRefreshTokenException;
import com.harsha.bff_service.security.adapter.CustomUserDetails;
import com.harsha.bff_service.security.entity.UserEntity;
import com.harsha.bff_service.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken
                                .unauthenticated(
                                        request.username(),
                                        request.password()
                                )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        UserEntity user = userDetails.getUser();

        //Delete Previous refresh tokens
        refreshTokenService.revokeRefreshTokensByUser(user);

        String accessToken =
                jwtService.generateAccessToken(authentication);

        RefreshTokenEntity refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    public LoginResponse refreshToken(
            RefreshTokenRequest request
    ) {
        RefreshTokenEntity refreshToken = refreshTokenService
                .findByToken(request.refreshToken())
                .orElseThrow(() ->
                        new InvalidRefreshTokenException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);

        UserEntity user = refreshToken.getUser();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getUsername());

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        String accessToken =
                jwtService.generateAccessToken(authentication);

        refreshTokenService.revokeRefreshToken(refreshToken);

        RefreshTokenEntity newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                newRefreshToken.getToken()
        );
    }

    public void logout(
            LogoutRequest request
    ) {
        RefreshTokenEntity refreshToken =
                refreshTokenService
                        .findByToken(request.refreshToken())
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Refresh token not found."
                                )
                        );

        refreshTokenService
                .revokeRefreshToken(refreshToken);
    }
}
