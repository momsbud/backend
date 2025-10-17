package com.momsbud.backend.referrals.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "referral_code",
        indexes = {
                @Index(name="referral_code_doctor_idx", columnList="doctor_user_id"),
                @Index(name="referral_code_active_idx", columnList="active")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReferralCode extends BaseEntity {

    @Column(name = "doctor_user_id", nullable = false, length = 26)
    private String doctorUserId;

    @Column(name = "code", nullable = false, unique = true, length = 32)
    private String code;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
