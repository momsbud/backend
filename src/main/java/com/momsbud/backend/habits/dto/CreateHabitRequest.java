package com.momsbud.backend.habits.dto;

import com.momsbud.backend.habits.model.HabitFrequency;
import com.momsbud.backend.habits.model.HabitType;
import lombok.*;

import java.time.OffsetTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateHabitRequest {
    private String title;
    private String description;

    private HabitFrequency frequency;       // DAILY / WEEKLY
    private List<Integer> daysOfWeek;       // 0..6, matches your logic in service

    private OffsetTime timeOfDay;
    private String timezone;

    // 🔹 NEW: we already wired this in service step 1.5
    private HabitType habitType;

    // 🔹 NEW: tags list, will be saved as text[]
    private List<String> tags;
}
