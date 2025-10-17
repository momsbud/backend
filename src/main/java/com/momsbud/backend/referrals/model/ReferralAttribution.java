package com.momsbud.backend.referrals.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "referral_attribution",
        uniqueConstraints = @UniqueConstraint(name="referral_attribution_uniq", columnNames = {"code_id","user_id"}),
        indexes = {
                @Index(name="referral_attr_user_idx", columnList="user_id"),
                @Index(name="referral_attr_code_idx", columnList="code_id")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReferralAttribution extends BaseEntity {

    @Column(name = "code_id", nullable = false, length = 26)
    private String codeId;

    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @Column(name = "attributed_at", nullable = false)
    private OffsetDateTime attributedAt = OffsetDateTime.now();
}
