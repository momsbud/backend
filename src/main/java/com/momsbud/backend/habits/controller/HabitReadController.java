package com.momsbud.backend.habits.controller;

import com.momsbud.backend.habits.dto.HabitResponse;
import com.momsbud.backend.habits.service.HabitReadService;
import com.momsbud.backend.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habits")
public class HabitReadController {

    private final HabitReadService habitReadService;

    @GetMapping("/assigned")
    public List<HabitResponse> assigned(@CurrentUser String userId) {
        return habitReadService.getAssignedHabits(userId);
    }

    @GetMapping("/today")
    public List<HabitResponse> today(@CurrentUser String userId,
                                     @RequestParam(name = "tz", required = false) String timezone) {
        return habitReadService.getTodayAssignedHabits(userId, timezone);
    }
}
