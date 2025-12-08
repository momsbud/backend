package com.momsbud.backend.tracking.service.impl;

import com.momsbud.backend.tracking.dto.MetricLogRequest;
import com.momsbud.backend.tracking.dto.MetricResponse;
import com.momsbud.backend.tracking.dto.MetricTypeResponse;
import com.momsbud.backend.tracking.dto.MetricTypeUpsertRequest;
import com.momsbud.backend.tracking.model.MetricType;
import com.momsbud.backend.tracking.model.UserMetric;
import com.momsbud.backend.tracking.repo.MetricTypeRepository;
import com.momsbud.backend.tracking.repo.UserMetricRepository;
import com.momsbud.backend.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final MetricTypeRepository typeRepo;
    private final UserMetricRepository metricRepo;

    // ----------------------------------------------------------------------
    // METRIC TYPE – READ (for app users, enabled only)
    // ----------------------------------------------------------------------
    @Override
    public List<MetricTypeResponse> listTypes() {
        return typeRepo.findAllByEnabledTrueAndIsDeletedFalseOrderByLabelAsc()
                .stream()
                .map(this::toTypeDto)
                .toList();
    }

    // ----------------------------------------------------------------------
    // METRIC TYPE – ADMIN CRUD (for seeding & management)
    // ----------------------------------------------------------------------

    /**
     * Create a new metric type (e.g. weight_kg, steps, sleep_hours, water_ml).
     */
    public MetricTypeResponse createMetricType(MetricTypeUpsertRequest req) {
        Objects.requireNonNull(req.getCode(), "code is required");
        Objects.requireNonNull(req.getLabel(), "label is required");
        Objects.requireNonNull(req.getUnit(), "unit is required");

        typeRepo.findByCodeAndIsDeletedFalse(req.getCode())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Metric type with this code already exists: " + req.getCode());
                });

        MetricType mt = MetricType.builder()
                .code(req.getCode().trim().toLowerCase())
                .label(req.getLabel().trim())
                .unit(req.getUnit().trim())
                .enabled(req.isEnabled())
                .build();

        mt = typeRepo.save(mt);
        return toTypeDto(mt);
    }

    /**
     * Update an existing metric type by code.
     */
    public MetricTypeResponse updateMetricType(String code, MetricTypeUpsertRequest req) {
        MetricType mt = typeRepo.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new NoSuchElementException("Metric type not found: " + code));

        if (req.getLabel() != null && !req.getLabel().isBlank()) {
            mt.setLabel(req.getLabel().trim());
        }
        if (req.getUnit() != null && !req.getUnit().isBlank()) {
            mt.setUnit(req.getUnit().trim());
        }
        // We usually do not change "code" to keep references stable.
        // If you really want to, you can allow updating code here.
        mt.setEnabled(req.isEnabled());

        mt = typeRepo.save(mt);
        return toTypeDto(mt);
    }

    /**
     * Disable + soft-delete a metric type by code.
     */
    public void deleteMetricType(String code) {
        MetricType mt = typeRepo.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new NoSuchElementException("Metric type not found: " + code));

        mt.setEnabled(false);
        mt.setDeleted(true); // BaseEntity has setDeleted(true) as you already use in UserMetric
        typeRepo.save(mt);
    }

    // ----------------------------------------------------------------------
    // USER METRICS (existing behavior)
    // ----------------------------------------------------------------------

    @Override
    public MetricResponse log(String userId, MetricLogRequest req) {
        // Validate & fetch type
        MetricType type = typeRepo.findByCodeAndIsDeletedFalse(req.getTypeCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown metric type: " + req.getTypeCode()));

        // Build metric
        UserMetric m = UserMetric.builder()
                .userId(userId)
                .typeCode(type.getCode())
                .value(BigDecimal.valueOf(req.getValue()))
                .recordedAt(req.asRecordedAt())
                .timezone(
                        req.getTimezone() != null && !req.getTimezone().isBlank()
                                ? req.getTimezone()
                                : "Asia/Kolkata"
                )
                .notes(req.getNotes())
                .build();

        m = metricRepo.save(m);
        return toMetricDto(m, type);
    }

    @Override
    public List<MetricResponse> list(String userId, OffsetDateTime from, OffsetDateTime to, String typeCode) {
        if (from == null || to == null) {
            ZoneId ist = ZoneId.of("Asia/Kolkata");
            OffsetDateTime start = OffsetDateTime.now(ist)
                    .toLocalDate()
                    .atStartOfDay(ist)
                    .toOffsetDateTime();
            OffsetDateTime end = start.plusDays(1);
            from = (from == null) ? start : from;
            to = (to == null) ? end : to;
        }

        List<UserMetric> rows;
        if (typeCode != null && !typeCode.isBlank()) {
            rows = metricRepo.findAllByUserIdAndTypeCodeAndIsDeletedFalseOrderByRecordedAtDesc(userId, typeCode);
        } else {
            rows = metricRepo.findAllByUserIdAndRecordedAtBetweenAndIsDeletedFalseOrderByRecordedAtDesc(userId, from, to);
        }

        // Preload types to fill labels/units
        return rows.stream()
                .map(m -> {
                    MetricType t = typeRepo.findByCodeAndIsDeletedFalse(m.getTypeCode())
                            .orElse(MetricType.builder()
                                    .code(m.getTypeCode())
                                    .label(m.getTypeCode())
                                    .unit("")
                                    .enabled(true)
                                    .build());
                    return toMetricDto(m, t);
                })
                .toList();
    }

    @Override
    public MetricResponse latest(String userId, String typeCode) {
        UserMetric m = metricRepo.findFirstByUserIdAndTypeCodeAndIsDeletedFalseOrderByRecordedAtDesc(userId, typeCode)
                .orElseThrow(() -> new IllegalArgumentException("No data for type: " + typeCode));
        MetricType t = typeRepo.findByCodeAndIsDeletedFalse(typeCode)
                .orElse(MetricType.builder()
                        .code(typeCode)
                        .label(typeCode)
                        .unit("")
                        .enabled(true)
                        .build());
        return toMetricDto(m, t);
    }

    @Override
    public void delete(String userId, String metricId) {
        UserMetric m = metricRepo.findById(metricId)
                .orElseThrow(() -> new IllegalArgumentException("Metric not found"));
        if (!m.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not allowed");
        }
        m.setDeleted(true);
        metricRepo.save(m);
    }

    // ----------------------------------------------------------------------
    // MAPPERS
    // ----------------------------------------------------------------------

    private MetricTypeResponse toTypeDto(MetricType t) {
        return MetricTypeResponse.builder()
                .id(t.getId())
                .code(t.getCode())
                .label(t.getLabel())
                .unit(t.getUnit())
                .enabled(t.isEnabled())
                .build();
    }

    private MetricResponse toMetricDto(UserMetric m, MetricType t) {
        return MetricResponse.builder()
                .id(m.getId())
                .typeId(t.getCode())            // for older callers
                .typeCode(t.getCode())
                .typeLabel(t.getLabel())
                .unit(t.getUnit())
                .value(m.getValue().doubleValue())
                .timestamp(m.getRecordedAt().toInstant().toEpochMilli())
                .notes(m.getNotes())
                .build();
    }
}
