package com.momsbud.backend.referrals.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApplyCodeResponse {
    private String codeId;
    private String userId;
    private OffsetDateTime attributedAt;
    private boolean created; // false if idempotent repeat
}
