package com.momsbud.backend.habits.controller;

import com.momsbud.backend.habits.dto.*;
import com.momsbud.backend.habits.service.HabitService;
import com.momsbud.backend.habits.service.HabitSummaryService;
import com.momsbud.backend.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService service;
    private final HabitSummaryService habitSummaryService;

    @PostMapping
    public HabitResponse create(@RequestBody CreateHabitRequest req, Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return service.create(userId, req);
    }

    @GetMapping
    public List<HabitResponse> list(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return service.listMine(userId);
    }

    @PatchMapping("/{id}/active")
    public HabitResponse setActive(@PathVariable String id,
                                   @RequestBody UpdateHabitActiveRequest req,
                                   Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return service.setActive(userId, id, req.isActive());
    }

    @PostMapping("/{id}/log")
    public ResponseEntity<?> log(@PathVariable String id,
                                 @RequestBody LogHabitRequest req,
                                 Authentication auth) {
        String userId = (String) auth.getPrincipal();
        service.log(userId, id, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/v1/today")
    public List<TodayHabitItem> today(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return service.today(userId);
    }

    @GetMapping("/summary/week")
    public WeeklyHabitSummaryResponse weekSummary(
            @CurrentUser String userId,
            @RequestParam(name = "weekStart", required = false) LocalDate weekStart
    ) {
        return habitSummaryService.getWeeklySummary(userId, weekStart);
    }
}
