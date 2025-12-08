package com.momsbud.backend.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricTypeResponse {

    private String id;      // ULID

    private String code;    // e.g. "weight_kg"
    private String name;    // e.g. "Weight"
    private String unit;    // e.g. "kg"

    private boolean enabled;
}
