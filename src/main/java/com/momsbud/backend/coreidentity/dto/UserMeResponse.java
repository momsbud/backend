package com.momsbud.backend.coreidentity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserMeResponse {
    private String id;
    private String phone;
    private String email;
    private String userType; // e.g., USER/DOCTOR
    private String status;   // if you track ACTIVE/BLOCKED/etc.
    private OnboardingInfo onboarding;
}
