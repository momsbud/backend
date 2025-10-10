package com.momsbud.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OtpVerifyRequest {
    private String phone;
    private String code;
    private String deviceFingerprint;
}
