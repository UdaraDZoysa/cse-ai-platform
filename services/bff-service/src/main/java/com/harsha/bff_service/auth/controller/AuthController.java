package com.harsha.bff_service.auth.controller;

import com.harsha.bff_service.auth.dto.LoginRequest;
import com.harsha.bff_service.auth.dto.LoginResponse;
import com.harsha.bff_service.auth.service.AuthenticationService;
import jakarta.validation.Valid;
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
        String token = authenticationService.login(request);

        return new LoginResponse(token);
    }
}
