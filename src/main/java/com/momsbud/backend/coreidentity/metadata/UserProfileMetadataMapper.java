package com.momsbud.backend.coreidentity.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserProfileMetadataMapper {

    private final ObjectMapper objectMapper;

    public MaternityMetadata getMaternity(Map<String, Object> metadata) {
        if (metadata == null || !metadata.containsKey("Maternity")) return null;
        return objectMapper.convertValue(metadata.get("Maternity"), MaternityMetadata.class);
    }

    public void putMaternity(Map<String, Object> metadata, MaternityMetadata maternity) {
        if (metadata == null) metadata = new HashMap<>();
        maternity.normalize();
        metadata.put("Maternity", objectMapper.convertValue(maternity, Map.class));
    }
}
