package com.momsbud.backend.tracking.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.time.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MetricLogRequest {
    @NotBlank private String typeCode;  // stable key e.g., "weight_kg"
    @NotNull  private Double value;     // will be converted to BigDecimal
    @NotNull  private Long timestamp;   // epoch millis (client local time)
    private String timezone;            // optional (default IST)
    private String notes;

    // Back-compat aliases if older service code referenced these:
    public String getMetricKey() { return typeCode; }
    public Long getRecordedAtMillis() { return timestamp; }
    public String getNote() { return notes; }

    // Convenience for service (OffsetDateTime conversion here):
    public OffsetDateTime asRecordedAt() {
        ZoneId zone = (timezone != null && !timezone.isBlank()) ? ZoneId.of(timezone) : ZoneId.of("Asia/Kolkata");
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zone);
    }
}
