// src/main/java/com/momsbud/backend/habits/service/HabitReadService.java
package com.momsbud.backend.habits.service;

import com.momsbud.backend.habits.dto.HabitResponse;

import java.util.List;

public interface HabitReadService {
    List<HabitResponse> getAssignedHabits(String userId);
    List<HabitResponse> getTodayAssignedHabits(String userId, String timezone);
}
