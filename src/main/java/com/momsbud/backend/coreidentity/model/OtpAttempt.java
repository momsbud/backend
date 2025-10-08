package com.momsbud.backend.coreidentity.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "otp_attempt")
public class OtpAttempt extends BaseEntity {

    @Column(name = "user_id", length = 26)
    private String userId;               // nullable

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(name = "code_hash", nullable = false)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private OtpState state = OtpState.SENT;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "ip", columnDefinition = "inet")
    private String ip;                   // map inet as String

    @Column(name = "device_fingerprint")
    private String deviceFingerprint;

    @Column(name = "fail_reason")
    private String failReason;
}
