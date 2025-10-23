package com.momsbud.backend.habits.controller;

import com.momsbud.backend.habits.dto.*;
import com.momsbud.backend.habits.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService service;

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

    @GetMapping("/today")
    public List<TodayHabitItem> today(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return service.today(userId);
    }
}
