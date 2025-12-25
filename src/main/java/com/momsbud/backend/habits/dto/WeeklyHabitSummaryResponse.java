package com.momsbud.backend.habits.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WeeklyHabitSummaryResponse {

    private LocalDate weekStart; // Monday
    private LocalDate weekEnd;   // Sunday

    private int assignedHabits;  // number of active assigned habits
    private int totalScheduled;  // total scheduled occurrences in the week (based on frequency/days)
    private int totalDone;       // DONE logs within the week for assigned habits
    private double completionRate; // totalDone / totalScheduled

    private int perfectDays;     // days where all scheduled habits were DONE
    private int currentStreakDays; // consecutive perfectDays ending today (within this week)

    // ✅ NEW: today
    private int scheduledToday;  // scheduled occurrences for today across assigned habits
    private int doneToday;       // DONE occurrences for today across assigned habits
    private int missedToday;     // scheduledToday - doneToday

    // ✅ NEW: top habits this week
    private List<TopHabit> topHabits; // top 3 by completion rate (then scheduled)

    // ✅ NEW: week-over-week delta (this week vs previous week)
    private WeekOverWeekDelta weekOverWeek;

    private List<HabitBreakdown> habits;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class HabitBreakdown {
        private String habitId;
        private String title;
        private String habitType;

        private int scheduled;   // scheduled occurrences this week
        private int done;        // DONE occurrences this week
        private double completionRate;

        private List<DayStatus> days; // 7 entries (Mon..Sun)
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DayStatus {
        private LocalDate date;
        private boolean scheduled;
        private String status; // DONE | SKIPPED | null
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopHabit {
        private String habitId;
        private String title;
        private int scheduled;
        private int done;
        private double completionRate;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class WeekOverWeekDelta {
        private LocalDate prevWeekStart;
        private LocalDate prevWeekEnd;

        private int prevTotalScheduled;
        private int prevTotalDone;
        private double prevCompletionRate;

        private double completionRateDelta; // thisWeek - prevWeek
        private int doneDelta;              // thisWeek - prevWeek
        private int scheduledDelta;         // thisWeek - prevWeek
        private int perfectDaysDelta;       // thisWeek - prevWeek
    }
}
