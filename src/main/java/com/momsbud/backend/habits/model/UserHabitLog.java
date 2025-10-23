package com.momsbud.backend.habits.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "user_habit_log",
        uniqueConstraints = @UniqueConstraint(name="user_habit_log_unique", columnNames = {"habit_id","user_id","log_date"}),
        indexes = @Index(name="uhl_user_date_idx", columnList="user_id, log_date"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserHabitLog extends BaseEntity {

    @Column(name = "habit_id", nullable = false, length = 26)
    private String habitId;

    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, length = 16, columnDefinition = "habit_log_status")
    private HabitLogStatus status;

    @Column(name = "notes", length = 500)
    private String notes;

}
