package com.momsbud.backend.coreidentity.service;

import com.momsbud.backend.coreidentity.dto.UserMeResponse;

public interface UserQueryService {
    UserMeResponse getUserById(String userId);
}
