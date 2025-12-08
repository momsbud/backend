package com.momsbud.backend.habits.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.time.OffsetTime;
import java.util.Map;

@Entity
@Table(name = "habit", indexes = {
        @Index(name="habit_user_active_idx", columnList="user_id, active")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Habit extends BaseEntity {

    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "frequency", nullable = false, length = 16, columnDefinition = "habit_frequency")
    private HabitFrequency frequency = HabitFrequency.DAILY;

    // persisted as Postgres int[]
    @Column(name = "days_of_week", columnDefinition = "int[]")
    private Integer[] daysOfWeek;

    @Column(name = "time_of_day")
    private OffsetTime timeOfDay;

    @Column(name = "timezone", length = 64)
    private String timezone;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "habit_type", nullable = false, length = 32)
    private HabitType habitType = HabitType.GENERIC;

    // persisted as Postgres text[]
    @Column(name = "tags", columnDefinition = "text[]")
    private String[] tags;



}
