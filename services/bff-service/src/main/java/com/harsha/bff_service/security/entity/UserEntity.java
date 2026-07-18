package com.harsha.bff_service.security.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_details",
        indexes = {
                @Index(
                        name = "idx_username",
                        columnList = "username"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String keycloakUserId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String image;

    private String preferredName;
}
