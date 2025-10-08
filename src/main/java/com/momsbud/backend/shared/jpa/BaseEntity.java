package com.momsbud.backend.shared.jpa;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * Common columns for all first-class tables.
 * - id: ULID string (set by your service/factory; keep length=26)
 * - createdAt / updatedAt: auto-filled in UTC
 * - createdBy / updatedBy: optional auditing (fill in service layer)
 * - isDeleted: soft delete flag
 * - metadata: jsonb, flexible per-entity payload
 */
@Getter
@Setter
@MappedSuperclass
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    @EqualsAndHashCode.Include
    @Column(length = 26, nullable = false)
    @Id
    @GeneratedValue(generator = "ulid")
    @GenericGenerator(name = "ulid", strategy = "com.momsbud.backend.shared.jpa.id.UlidGenerator")
    protected String id; // ULID string; generate in your service before save

    @Column(name = "created_at", nullable = false)
    protected OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    protected OffsetDateTime updatedAt;

    @Column(name = "created_by", length = 26)
    protected String createdBy;

    @Column(name = "updated_by", length = 26)
    protected String updatedBy;

    @Column(name = "is_deleted", nullable = false)
    protected boolean isDeleted = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb", nullable = false)
    protected Map<String, Object> metadata = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        final var now = OffsetDateTime.now(ZoneOffset.UTC);
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (metadata == null) metadata = new HashMap<>();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
        if (metadata == null) metadata = new HashMap<>();
    }
}
