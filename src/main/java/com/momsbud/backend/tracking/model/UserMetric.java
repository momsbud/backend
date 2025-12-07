package com.momsbud.backend.tracking.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter @ToString(callSuper = true)
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "user_metric")
public class UserMetric extends BaseEntity {

    @Column(name = "user_id", length = 26, nullable = false)
    private String userId;

    @Column(name = "type_code", length = 64, nullable = false)
    private String typeCode; // reference metric_type.code

    @Column(name = "value", nullable = false, precision = 18, scale = 6)
    private BigDecimal value;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @Builder.Default
    @Column(name = "timezone", length = 64, nullable = false)
    private String timezone = "Asia/Kolkata";

    @Column(name = "notes", length = 500)
    private String notes;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata = new HashMap<>();
}
