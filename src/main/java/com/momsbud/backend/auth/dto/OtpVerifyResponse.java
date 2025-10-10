package com.momsbud.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OtpVerifyResponse {
    private String accessToken;
    private String userId;
    private String sessionId;
}
