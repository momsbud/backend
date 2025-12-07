package com.momsbud.backend.tracking.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MetricTypeResponse {
    private String id;      // ULID (from entity)
    private String code;    // e.g., weight_kg
    private String label;   // e.g., Weight (kg)
    private String unit;    // e.g., kg
    private boolean enabled;
}
