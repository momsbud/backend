package com.momsbud.backend.coreidentity.service;

import com.momsbud.backend.coreidentity.dto.ProfileMetadataPatchRequest;
import com.momsbud.backend.coreidentity.dto.UserProfileResponse;

public interface UserProfileWriteService {
    UserProfileResponse patchMetadata(String userId, ProfileMetadataPatchRequest request);
}
