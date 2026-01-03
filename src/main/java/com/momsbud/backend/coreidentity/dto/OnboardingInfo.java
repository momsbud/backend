package com.momsbud.backend.coreidentity.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OnboardingInfo {
    private OnboardingStatus status;
    private String nextAction;          // "ONBOARDING" | "DASHBOARD"
    private List<String> missingFields; // ["fullName","maternity.expectedDueDate","foodPreferences.dietType"]
}
