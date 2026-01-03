package com.momsbud.backend.coreidentity.service.impl;

import com.momsbud.backend.coreidentity.dto.UserMeResponse;
import com.momsbud.backend.coreidentity.model.User;
import com.momsbud.backend.coreidentity.repo.UserRepository;
import com.momsbud.backend.coreidentity.service.OnboardingService;
import com.momsbud.backend.coreidentity.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final OnboardingService onboardingService;

    @Override
    public UserMeResponse getUserById(String userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound("User not found"));

        return UserMeResponse.builder()
                .id(u.getId())
                .phone(u.getPhone())
                .email(u.getEmail())
                .userType(u.getUserType() != null ? u.getUserType().name() : "USER")
                .status(u.getStatus())
                .onboarding(onboardingService.evaluate(u.getId()))
                .build();
    }

    public static class NotFound extends RuntimeException {
        public NotFound(String msg) { super(msg); }
    }
}
