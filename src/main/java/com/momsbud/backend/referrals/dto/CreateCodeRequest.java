package com.momsbud.backend.referrals.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateCodeRequest {
    private String code; // optional; if missing, auto-generate
}
