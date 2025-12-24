package com.momsbud.backend.habits.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "habit_assignment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitAssignmentEntity extends BaseEntity {

    @Column(name = "user_id", length = 26, nullable = false)
    private String userId;

    @Column(name = "habit_id", length = 26, nullable = false)
    private String habitId;

    @Column(name = "rule_id", length = 26)
    private String ruleId;

    @Column(name = "status", length = 20, nullable = false)
    private String status; // ACTIVE | ENDED | PAUSED

    @Column(name = "assigned_from", nullable = false)
    private OffsetDateTime assignedFrom;

    @Column(name = "assigned_until")
    private OffsetDateTime assignedUntil;

    @Column(name = "dedupe_key")
    private String dedupeKey;

    @Column(name = "reason")
    private String reason;
}
