package com.momsbud.backend.habits.dto;

import com.momsbud.backend.habits.model.HabitFrequency;
import com.momsbud.backend.habits.model.HabitLogStatus;
import com.momsbud.backend.habits.model.HabitType;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayHabitItem {

    private String id;

    private String title;

    private HabitFrequency frequency;
    private List<Integer> daysOfWeek;

    private OffsetTime timeOfDay;
    private String timezone;

    private boolean active;

    private LocalDate date;              // the date this item is for (today in user’s TZ)
    private HabitLogStatus lastStatus;   // DONE / SKIPPED / null

    private HabitType habitType;

    private List<String> tags;
}
