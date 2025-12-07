package com.momsbud.backend.tracking.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MetricResponse {
    private String id;

    // Keep both for compatibility & clarity
    private String typeId;      // same as typeCode; for older callers using builder.typeId(...)
    private String typeCode;    // stable key e.g., "weight_kg"
    private String typeLabel;   // e.g., "Weight (kg)"
    private String unit;        // "kg", "cm", etc.

    private Double value;       // BigDecimal -> double on response
    private Long timestamp;     // epoch millis (from recordedAt)
    private String notes;
}
