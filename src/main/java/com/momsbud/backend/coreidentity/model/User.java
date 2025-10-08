package com.momsbud.backend.coreidentity.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "users")
public class User extends BaseEntity {

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 255)
    private String email; // DB type is CITEXT

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType = UserType.CUSTOMER;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(name = "auth_provider", nullable = false)
    private String authProvider = "OTP";

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;
}
