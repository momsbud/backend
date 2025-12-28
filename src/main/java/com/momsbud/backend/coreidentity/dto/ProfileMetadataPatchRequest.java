package com.momsbud.backend.coreidentity.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileMetadataPatchRequest {

    /**
     * Keys/sections to merge into existing metadata.
     * Example:
     * {
     *   "Maternity": {"gestationWeek": 24, "trimester": 2}
     * }
     */
    private Map<String, Object> patch;

    /**
     * Optional dot-path keys to remove from metadata.
     * Examples:
     *  - "Maternity.riskFlags.gdm"
     *  - "Agriculture.cropType"
     */
    private List<String> remove;
}
