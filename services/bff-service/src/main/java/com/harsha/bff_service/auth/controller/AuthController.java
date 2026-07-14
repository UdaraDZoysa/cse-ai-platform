package com.harsha.bff_service.auth.controller;

import com.harsha.bff_service.auth.dto.LoginRequest;
import com.harsha.bff_service.auth.dto.LoginResponse;
import com.harsha.bff_service.auth.dto.LogoutRequest;
import com.harsha.bff_service.auth.dto.RefreshTokenRequest;
import com.harsha.bff_service.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authenticationService.login(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                authenticationService.refreshToken(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        authenticationService.logout(request);

        return ResponseEntity.noContent().build();
    }
}
