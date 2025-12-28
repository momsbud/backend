package com.momsbud.backend.coreidentity.dto;

import com.momsbud.backend.coreidentity.metadata.MaternityMetadata;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MaternityMetadataRequest {
    private MaternityMetadata maternity;
}
