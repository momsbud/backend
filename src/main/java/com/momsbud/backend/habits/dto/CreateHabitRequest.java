package com.momsbud.backend.habits.dto;

import com.momsbud.backend.habits.model.HabitFrequency;
import lombok.*;

import java.time.OffsetTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateHabitRequest {
    private String title;
    private String description;
    private HabitFrequency frequency;       // DAILY/WEEKLY
    private List<Integer> daysOfWeek;       // for WEEKLY: 0=Sun..6=Sat
    private OffsetTime timeOfDay;           // optional
    private String timezone;                // optional IANA
}
