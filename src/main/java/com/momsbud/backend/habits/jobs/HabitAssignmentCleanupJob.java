package com.momsbud.backend.habits.jobs;

import com.momsbud.backend.habits.service.HabitAssignmentCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HabitAssignmentCleanupJob {

    private final HabitAssignmentCleanupService cleanupService;

    /**
     * Runs daily at 03:00 AM UTC
     * (08:30 IST)
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredAssignments() {
        int ended = cleanupService.cleanupExpiredAssignments();
        log.debug("HabitAssignmentCleanupJob finished. ended={}", ended);
    }
}
