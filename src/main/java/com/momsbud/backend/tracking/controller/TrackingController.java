package com.momsbud.backend.tracking.controller;

import com.momsbud.backend.tracking.dto.*;
import com.momsbud.backend.tracking.service.TrackingService;
import com.momsbud.backend.security.CurrentUser; // your helper; replace with your actual mechanism
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/types")
    public ResponseEntity<List<MetricTypeResponse>> types() {
        return ResponseEntity.ok(trackingService.listTypes());
    }

    @PostMapping("/logs")
    public ResponseEntity<MetricResponse> log(@CurrentUser String userId,
                                              @Valid @RequestBody MetricLogRequest req) {
        return ResponseEntity.ok(trackingService.log(userId, req));
    }

    @GetMapping
    public ResponseEntity<List<MetricResponse>> list(@CurrentUser String userId,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                     OffsetDateTime from,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                     OffsetDateTime to,
                                                     @RequestParam(required = false) String typeCode) {
        return ResponseEntity.ok(trackingService.list(userId, from, to, typeCode));
    }

    @GetMapping("/latest")
    public ResponseEntity<MetricResponse> latest(@CurrentUser String userId,
                                                 @RequestParam String typeCode) {
        return ResponseEntity.ok(trackingService.latest(userId, typeCode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CurrentUser String userId, @PathVariable String id) {
        trackingService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
