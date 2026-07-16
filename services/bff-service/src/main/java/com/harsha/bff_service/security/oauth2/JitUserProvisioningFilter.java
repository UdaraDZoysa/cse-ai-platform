package com.harsha.bff_service.security.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JitUserProvisioningFilter extends OncePerRequestFilter {
    private final JitUserProvisioningService provisioningService;

    public JitUserProvisioningFilter(
            JitUserProvisioningService provisioningService
    ) {
        this.provisioningService = provisioningService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            provisioningService.provision(jwtAuthenticationToken.getToken());
        }

        filterChain.doFilter(request, response);
    }
}
