package com.momsbud.backend.coreidentity.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "user_session")
public class UserSession extends BaseEntity {

    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @Column(name = "jti", nullable = false, columnDefinition = "uuid")
    private UUID jti;

    @Column(name = "last_seen_at", nullable = false)
    private OffsetDateTime lastSeenAt;

    @Column(name = "first_ip", columnDefinition = "inet")
    private String firstIp;

    @Column(name = "last_ip", columnDefinition = "inet")
    private String lastIp;

    @Column(name = "device_fingerprint")
    private String deviceFingerprint;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;
}
