package com.momsbud.backend.coreidentity.controller;

import com.momsbud.backend.coreidentity.dto.MaternityMetadataRequest;
import com.momsbud.backend.coreidentity.metadata.UserProfileMetadataMapper;
import com.momsbud.backend.coreidentity.repo.UserProfileRepository;
import com.momsbud.backend.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class MaternityProfileController {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMetadataMapper mapper;

    @PatchMapping("/maternity")
    public void updateMaternity(
            @CurrentUser String userId,
            @RequestBody MaternityMetadataRequest request
    ) {
        var profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Profile not found"));

        var metadata = profile.getMetadata();
        mapper.putMaternity(metadata, request.getMaternity());

        profile.setMetadata(metadata);
        userProfileRepository.save(profile);
    }
}
