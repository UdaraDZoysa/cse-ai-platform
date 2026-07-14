package com.harsha.bff_service.security.config;

import com.harsha.bff_service.security.entity.Role;
import com.harsha.bff_service.security.entity.UserEntity;
import com.harsha.bff_service.security.repository.JpaUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DetailInitializer {
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public DetailInitializer(
            JpaUserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initUsers() {
        return args -> {
            createUser(
                    "harsha",
                    "password123",
                    Role.ADMIN,
                    "HarshaDeZoysa",
                    "hudaragc@gmail.com",
                    "0764745381"
            );

            createUser(
                    "osanda",
                    "osanda123",
                    Role.USER,
                    "Osanda",
                    "osanda@gmail.com",
                    "0771234567"
            );

            createUser(
                    "Inuki",
                    "inuki",
                    Role.USER,
                    "Inuki",
                    "inuki@gmail.com",
                    "0761234567"
            );
        };
    }

    private void createUser(
            String username,
            String password,
            Role role,
            String preferredName,
            String email,
            String phone
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .preferredName(preferredName)
                .email(email)
                .phone(phone)
                .build();

        userRepository.save(user);
    }
}
