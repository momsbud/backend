package com.momsbud.backend.tracking.controller;

import com.momsbud.backend.tracking.dto.MetricTypeResponse;
import com.momsbud.backend.tracking.dto.MetricTypeUpsertRequest;
import com.momsbud.backend.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracking/admin/metric-types")
public class MetricTypeAdminController {

    private final TrackingService trackingService;

    /**
     * List metric types.
     * For now this uses listTypes(), which returns enabled metric types.
     * If later you want a full admin view (including disabled/deleted),
     * we can add a separate method in the service.
     */
    @GetMapping
    public List<MetricTypeResponse> listAll() {
        return trackingService.listTypes();
    }

    /**
     * Create a new metric type (e.g. weight_kg, steps, sleep_hours, water_ml).
     */
    @PostMapping
    public MetricTypeResponse create(@RequestBody MetricTypeUpsertRequest req) {
        return trackingService.createMetricType(req);
    }

    /**
     * Update an existing metric type by its code.
     */
    @PutMapping("/{code}")
    public MetricTypeResponse update(@PathVariable String code,
                                     @RequestBody MetricTypeUpsertRequest req) {
        return trackingService.updateMetricType(code, req);
    }

    /**
     * Disable + soft-delete a metric type by its code.
     */
    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code) {
        trackingService.deleteMetricType(code);
    }
}
