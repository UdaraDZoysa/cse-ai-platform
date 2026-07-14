package com.harsha.bff_service.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginResponse(
        String accessToken
) {
}
