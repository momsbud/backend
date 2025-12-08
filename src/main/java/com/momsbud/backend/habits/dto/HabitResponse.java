package com.momsbud.backend.habits.dto;

import com.momsbud.backend.habits.model.HabitFrequency;
import com.momsbud.backend.habits.model.HabitType;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitResponse {

    private String id;

    private String title;
    private String description;

    private HabitFrequency frequency;
    private List<Integer> daysOfWeek;

    private OffsetTime timeOfDay;
    private String timezone;

    private boolean active;
    private OffsetDateTime createdAt;

    // 🔹 NEW
    private HabitType habitType;

    // 🔹 NEW
    private List<String> tags;
}
