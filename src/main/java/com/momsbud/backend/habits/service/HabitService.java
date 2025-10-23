package com.momsbud.backend.habits.service;

import com.momsbud.backend.habits.dto.*;
import java.util.List;

public interface HabitService {
    HabitResponse create(String userId, CreateHabitRequest req);
    List<HabitResponse> listMine(String userId);
    HabitResponse setActive(String userId, String habitId, boolean active);
    void log(String userId, String habitId, LogHabitRequest req);
    List<TodayHabitItem> today(String userId);
}
