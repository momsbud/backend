package com.momsbud.backend.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for creating/updating a metric type.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricTypeUpsertRequest {

    /**
     * A unique code like "weight_kg", "steps", "sleep_hours", "water_ml".
     */
    private String code;

    /**
     * Human-friendly name like "Weight", "Daily Steps", "Sleep Duration".
     */
    private String name;

    /**
     * Unit label like "kg", "steps", "hours", "ml".
     */
    private String unit;

    /**
     * Whether this metric type is active/visible in the app.
     */
    private boolean enabled;
}
