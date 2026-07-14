package com.harsha.bff_service.auth.dto;

public record LogoutRequest(
        String refreshToken
) {
}
