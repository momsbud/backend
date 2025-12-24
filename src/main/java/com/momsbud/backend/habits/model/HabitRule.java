package com.momsbud.backend.habits.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "habit_rule")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class HabitRule extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lob", nullable = false)
    private String lob;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "priority", nullable = false)
    private int priority = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "match", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> match = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "action", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> action = new HashMap<>();

    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo;
}
