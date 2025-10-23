package com.momsbud.backend.habits.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateHabitActiveRequest {
    private boolean active;
}
