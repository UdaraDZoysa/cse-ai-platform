package com.harsha.bff_service.security.oauth2;

import com.harsha.bff_service.security.entity.UserEntity;
import com.harsha.bff_service.security.repository.JpaUserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JitUserProvisioningService {
    private final JpaUserRepository userRepository;

    public JitUserProvisioningService(
            JpaUserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserEntity provision(Jwt jwt) {
        String keycloakUserId = jwt.getSubject();

        return userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseGet(() -> createUser(jwt));
    }

    private UserEntity createUser(Jwt jwt) {
        UserEntity user = UserEntity.builder()
                .keycloakUserId(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .build();

        return userRepository.save(user);
    }
}
