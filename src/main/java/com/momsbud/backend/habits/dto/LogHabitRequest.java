package com.momsbud.backend.habits.dto;

import com.momsbud.backend.habits.model.HabitLogStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LogHabitRequest {
    private HabitLogStatus status;  // DONE | SKIPPED
    private String notes;
}
