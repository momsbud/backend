package com.momsbud.backend.coreidentity.service;

import com.momsbud.backend.coreidentity.dto.OnboardingInfo;

public interface OnboardingService {
    OnboardingInfo evaluate(String userId);
}
