package com.momsbud.backend.coreidentity.controller;

import com.momsbud.backend.coreidentity.dto.ProfileMetadataPatchRequest;
import com.momsbud.backend.coreidentity.dto.UserProfileResponse;
import com.momsbud.backend.coreidentity.service.UserProfileWriteService;
import com.momsbud.backend.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileWriteService userProfileWriteService;

    @PatchMapping("/metadata")
    public UserProfileResponse patchMetadata(
            @CurrentUser String userId,
            @RequestBody ProfileMetadataPatchRequest request
    ) {
        return userProfileWriteService.patchMetadata(userId, request);
    }
}
