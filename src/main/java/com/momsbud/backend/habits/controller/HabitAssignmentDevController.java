package com.momsbud.backend.habits.controller;

import com.momsbud.backend.habits.service.HabitAssignmentRefreshService;
import com.momsbud.backend.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habits/assigned")
@ConditionalOnProperty(prefix = "momsbud.dev.endpoints", name = "enabled", havingValue = "true")
public class HabitAssignmentDevController {

    private final HabitAssignmentRefreshService refreshService;

    @PostMapping("/refresh")
    public HabitAssignmentRefreshService.RefreshResult refresh(
            @CurrentUser String userId,
            @RequestParam(defaultValue = "Maternity") String lob
    ) {
        return refreshService.refreshForUser(userId, lob);
    }
}
