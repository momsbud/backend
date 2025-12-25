package com.momsbud.backend.habits.service;

import com.momsbud.backend.habits.dto.WeeklyHabitSummaryResponse;

import java.time.LocalDate;

public interface HabitSummaryService {
    WeeklyHabitSummaryResponse getWeeklySummary(String userId, LocalDate weekStart);
}
