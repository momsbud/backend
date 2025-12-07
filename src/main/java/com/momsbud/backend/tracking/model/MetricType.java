package com.momsbud.backend.tracking.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter @ToString(callSuper = true)
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "metric_type")
public class MetricType extends BaseEntity {

    @Column(name = "code", length = 64, nullable = false, unique = true)
    private String code;

    @Column(name = "label", length = 128, nullable = false)
    private String label;

    @Column(name = "unit", length = 32, nullable = false)
    private String unit; // varchar (no DB enum)

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> metadata = new HashMap<>();
}
