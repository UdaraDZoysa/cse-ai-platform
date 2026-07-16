package com.harsha.bff_service.security.currentuser;

import java.util.Set;

public record CurrentUser(
        Long userId,
        String keycloakUserId,
        String username,
        String email,
        Set<String> roles
) {
}
