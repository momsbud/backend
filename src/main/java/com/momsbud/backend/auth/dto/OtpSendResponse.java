package com.momsbud.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OtpSendResponse {
    private String requestId;
    private long expiresInSeconds;
}
