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

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    private String image;

    private String preferredName;

    private boolean enabled;

    private boolean accountNonExpired;

    private boolean credentialsNonExpired;

    private boolean accountNonLocked;

    @PrePersist
    protected void onCreate() {
        accountNonExpired = true;
        credentialsNonExpired = true;
        accountNonLocked = true;
        enabled = true;
    }
}
