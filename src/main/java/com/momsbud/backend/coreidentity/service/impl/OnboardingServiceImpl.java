package com.momsbud.backend.coreidentity.service.impl;

import com.momsbud.backend.coreidentity.dto.*;
import com.momsbud.backend.coreidentity.metadata.UserProfileMetadataMapper;
import com.momsbud.backend.coreidentity.repo.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements com.momsbud.backend.coreidentity.service.OnboardingService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMetadataMapper metadataMapper;

    @Override
    public OnboardingInfo evaluate(String userId) {
        var profile = userProfileRepository.findByUserId(userId).orElse(null);
        List<String> missing = new ArrayList<>();

        // Profile missing entirely => definitely onboarding
        if (profile == null) {
            missing.add("profile");
            return OnboardingInfo.builder()
                    .status(OnboardingStatus.NOT_STARTED)
                    .nextAction("ONBOARDING")
                    .missingFields(missing)
                    .build();
        }

        // 1) Full name required
        if (profile.getFullName() == null || profile.getFullName().isBlank()) {
            missing.add("fullName");
        }

        // 2) Maternity required: at least one timeline signal
        var md = profile.getMetadata();
        var maternity = metadataMapper.getMaternity(md);
        boolean hasTimeline =
                maternity != null && (
                        maternity.getExpectedDueDate() != null ||
                                maternity.getLmpDate() != null ||
                                maternity.getGestationWeek() != null
                );
        if (!hasTimeline) {
            missing.add("maternity.expectedDueDate|lmpDate|gestationWeek");
        }

        // 3) Food preferences required (you decide schema; this checks a simple convention)
        // Convention: metadata["FoodPreferences"] is a Map and must have "dietType"
        Object fp = (md == null) ? null : md.get("FoodPreferences");
        String dietType = null;
        if (fp instanceof Map<?, ?> m) {
            Object v = m.get("dietType");
            if (v != null) dietType = String.valueOf(v);
        }
        if (dietType == null || dietType.isBlank()) {
            missing.add("foodPreferences.dietType");
        }

        OnboardingStatus status;
        if (missing.isEmpty()) status = OnboardingStatus.COMPLETED;
        else if (missing.size() == 3) status = OnboardingStatus.NOT_STARTED;
        else status = OnboardingStatus.PENDING_REQUIRED;

        String next = (status == OnboardingStatus.COMPLETED) ? "DASHBOARD" : "ONBOARDING";

        return OnboardingInfo.builder()
                .status(status)
                .nextAction(next)
                .missingFields(missing)
                .build();
    }
}
