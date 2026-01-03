package com.momsbud.backend.auth.dto;

import com.momsbud.backend.coreidentity.dto.OnboardingInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OtpVerifyResponse {
    private String accessToken;
    private String userId;
    private String sessionId;
    private OnboardingInfo onboarding;
}
