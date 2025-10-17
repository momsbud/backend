package com.momsbud.backend.referrals.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CodeResponse {
    private String id;
    private String code;
    private boolean active;
    private OffsetDateTime createdAt;
    private long attributions;
}
