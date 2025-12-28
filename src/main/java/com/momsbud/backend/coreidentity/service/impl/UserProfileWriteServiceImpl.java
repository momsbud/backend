package com.momsbud.backend.coreidentity.service.impl;

import com.momsbud.backend.coreidentity.dto.ProfileMetadataPatchRequest;
import com.momsbud.backend.coreidentity.dto.UserProfileResponse;
import com.momsbud.backend.coreidentity.model.UserProfile;
import com.momsbud.backend.coreidentity.repo.UserProfileRepository;
import com.momsbud.backend.coreidentity.service.UserProfileWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserProfileWriteServiceImpl implements UserProfileWriteService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public UserProfileResponse patchMetadata(String userId, ProfileMetadataPatchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        var profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("UserProfile not found for userId=" + userId));

        Map<String, Object> md = profile.getMetadata();
        if (md == null) md = new HashMap<>();

        Map<String, Object> patch = request.getPatch();
        if (patch != null && !patch.isEmpty()) {
            deepMerge(md, patch);
        }

        List<String> remove = request.getRemove();
        if (remove != null && !remove.isEmpty()) {
            for (String path : remove) {
                if (path == null || path.isBlank()) continue;
                removeDotPath(md, path.trim());
            }
        }

        profile.setMetadata(md);
        var saved = userProfileRepository.save(profile);

        return toResponse(saved);
    }

    private UserProfileResponse toResponse(UserProfile p) {
        return UserProfileResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .fullName(p.getFullName())
                .dob(p.getDob())
                .tz(p.getTz())
                .locale(p.getLocale())
                .metadata(p.getMetadata())
                .build();
    }

    @SuppressWarnings("unchecked")
    private void deepMerge(Map<String, Object> target, Map<String, Object> patch) {
        for (Map.Entry<String, Object> e : patch.entrySet()) {
            String key = e.getKey();
            Object patchVal = e.getValue();

            if (patchVal == null) {
                // If patch explicitly sets null, we set null (caller can also use remove[])
                target.put(key, null);
                continue;
            }

            Object existing = target.get(key);

            if (existing instanceof Map<?, ?> existingMap && patchVal instanceof Map<?, ?> patchMap) {
                // recursive merge
                Map<String, Object> existingCasted = (Map<String, Object>) existingMap;
                Map<String, Object> patchCasted = (Map<String, Object>) patchMap;
                deepMerge(existingCasted, patchCasted);
                target.put(key, existingCasted);
            } else {
                // arrays/lists/scalars replace
                target.put(key, patchVal);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void removeDotPath(Map<String, Object> root, String dotPath) {
        String[] parts = dotPath.split("\\.");
        if (parts.length == 0) return;

        Map<String, Object> curr = root;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = curr.get(parts[i]);
            if (!(next instanceof Map<?, ?>)) return; // nothing to remove
            curr = (Map<String, Object>) next;
        }
        curr.remove(parts[parts.length - 1]);
    }
}
