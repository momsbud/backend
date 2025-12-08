package com.momsbud.backend.tracking.service;

import com.momsbud.backend.tracking.dto.*;

import java.time.OffsetDateTime;
import java.util.List;

public interface TrackingService {
    // ---------- METRIC TYPE CRUD (ADMIN) ----------

    List<MetricTypeResponse> listMetricTypes();

    MetricTypeResponse createMetricType(MetricTypeUpsertRequest req);

    MetricTypeResponse updateMetricType(String code, MetricTypeUpsertRequest req);

    void deleteMetricType(String code);  // you can soft-delete or disable

    // ---------- EXISTING USER METRIC METHODS BELOW ----------
    List<MetricTypeResponse> listTypes();

    MetricResponse log(String userId, MetricLogRequest req);

    List<MetricResponse> list(String userId, OffsetDateTime from, OffsetDateTime to, String typeCode);

    MetricResponse latest(String userId, String typeCode);

    void delete(String userId, String metricId);
}
