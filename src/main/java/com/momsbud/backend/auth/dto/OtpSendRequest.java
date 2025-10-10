package com.momsbud.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OtpSendRequest {
    private String phone;
    private String deviceFingerprint;
}
