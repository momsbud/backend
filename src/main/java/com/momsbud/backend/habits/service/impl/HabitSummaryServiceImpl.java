package com.momsbud.backend.habits.service.impl;

import com.momsbud.backend.coreidentity.repo.UserProfileRepository;
import com.momsbud.backend.habits.dto.WeeklyHabitSummaryResponse;
import com.momsbud.backend.habits.model.Habit;
import com.momsbud.backend.habits.model.HabitFrequency;
import com.momsbud.backend.habits.model.HabitLogStatus;
import com.momsbud.backend.habits.repo.HabitAssignmentRepository;
import com.momsbud.backend.habits.repo.HabitRepository;
import com.momsbud.backend.habits.repo.UserHabitLogRepository;
import com.momsbud.backend.habits.service.HabitSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitSummaryServiceImpl implements HabitSummaryService {

    private final HabitAssignmentRepository habitAssignmentRepository;
    private final HabitRepository habitRepository;
    private final UserHabitLogRepository userHabitLogRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public WeeklyHabitSummaryResponse getWeeklySummary(String userId, LocalDate weekStart) {
        ZoneId zone = resolveUserZone(userId);

        LocalDate start = (weekStart != null)
                ? weekStart
                : LocalDate.now(zone).with(DayOfWeek.MONDAY);

        LocalDate end = start.plusDays(6);

        // 1) Active assigned habits
        var assignments = habitAssignmentRepository.findActiveByUserId(userId);
        if (assignments == null || assignments.isEmpty()) {
            return empty(start, end);
        }

        List<String> habitIds = assignments.stream()
                .map(a -> a.getHabitId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        var habits = habitRepository.findAllByIdInAndIsDeletedFalse(habitIds);
        if (habits == null || habits.isEmpty()) {
            return empty(start, end);
        }

        Map<String, Habit> habitById = habits.stream().collect(Collectors.toMap(Habit::getId, h -> h));

        // keep stable order as per assignments
        List<Habit> orderedHabits = new ArrayList<>();
        for (String id : habitIds) {
            Habit h = habitById.get(id);
            if (h != null && h.isActive()) orderedHabits.add(h);
        }
        if (orderedHabits.isEmpty()) {
            return empty(start, end);
        }

        // 2) Logs (current week)
        var logs = userHabitLogRepository.findAllByUserIdAndLogDateBetweenAndIsDeletedFalse(userId, start, end);
        Map<String, Map<LocalDate, HabitLogStatus>> statusByHabitByDate = indexLogsForAssigned(logs, habitById);

        // 3) Breakdown + totals
        WeekStats current = computeWeekStats(orderedHabits, statusByHabitByDate, start, zone);

        // 4) Week-over-week (previous week)
        LocalDate prevStart = start.minusDays(7);
        LocalDate prevEnd = prevStart.plusDays(6);

        var prevLogs = userHabitLogRepository.findAllByUserIdAndLogDateBetweenAndIsDeletedFalse(userId, prevStart, prevEnd);
        Map<String, Map<LocalDate, HabitLogStatus>> prevStatusByHabitByDate = indexLogsForAssigned(prevLogs, habitById);

        WeekStats prev = computeWeekStats(orderedHabits, prevStatusByHabitByDate, prevStart, zone);

        WeeklyHabitSummaryResponse.WeekOverWeekDelta wow = WeeklyHabitSummaryResponse.WeekOverWeekDelta.builder()
                .prevWeekStart(prevStart)
                .prevWeekEnd(prevEnd)
                .prevTotalScheduled(prev.totalScheduled)
                .prevTotalDone(prev.totalDone)
                .prevCompletionRate(prev.totalScheduled == 0 ? 0.0 : round2((double) prev.totalDone / prev.totalScheduled))
                .completionRateDelta(round2(current.completionRate - (prev.totalScheduled == 0 ? 0.0 : (double) prev.totalDone / prev.totalScheduled)))
                .doneDelta(current.totalDone - prev.totalDone)
                .scheduledDelta(current.totalScheduled - prev.totalScheduled)
                .perfectDaysDelta(current.perfectDays - prev.perfectDays)
                .build();

        return WeeklyHabitSummaryResponse.builder()
                .weekStart(start)
                .weekEnd(end)
                .assignedHabits(orderedHabits.size())

                .totalScheduled(current.totalScheduled)
                .totalDone(current.totalDone)
                .completionRate(round2(current.completionRate))

                .perfectDays(current.perfectDays)
                .currentStreakDays(current.currentStreakDays)

                .scheduledToday(current.scheduledToday)
                .doneToday(current.doneToday)
                .missedToday(Math.max(0, current.scheduledToday - current.doneToday))

                .topHabits(current.topHabits)

                .weekOverWeek(wow)

                .habits(current.breakdowns)
                .build();
    }

    // ---------- Helpers ----------

    private WeeklyHabitSummaryResponse empty(LocalDate start, LocalDate end) {
        return WeeklyHabitSummaryResponse.builder()
                .weekStart(start)
                .weekEnd(end)
                .assignedHabits(0)
                .totalScheduled(0)
                .totalDone(0)
                .completionRate(0.0)
                .perfectDays(0)
                .currentStreakDays(0)
                .scheduledToday(0)
                .doneToday(0)
                .missedToday(0)
                .topHabits(List.of())
                .weekOverWeek(null)
                .habits(List.of())
                .build();
    }

    private Map<String, Map<LocalDate, HabitLogStatus>> indexLogsForAssigned(
            List<com.momsbud.backend.habits.model.UserHabitLog> logs,
            Map<String, Habit> habitById
    ) {
        Map<String, Map<LocalDate, HabitLogStatus>> statusByHabitByDate = new HashMap<>();
        if (logs == null) return statusByHabitByDate;

        for (var l : logs) {
            if (!habitById.containsKey(l.getHabitId())) continue; // only assigned habits
            statusByHabitByDate
                    .computeIfAbsent(l.getHabitId(), k -> new HashMap<>())
                    .put(l.getLogDate(), l.getStatus());
        }
        return statusByHabitByDate;
    }

    private WeekStats computeWeekStats(
            List<Habit> orderedHabits,
            Map<String, Map<LocalDate, HabitLogStatus>> statusByHabitByDate,
            LocalDate weekStart,
            ZoneId zone
    ) {
        LocalDate weekEnd = weekStart.plusDays(6);

        int totalScheduled = 0;
        int totalDone = 0;

        List<WeeklyHabitSummaryResponse.HabitBreakdown> breakdowns = new ArrayList<>();

        // today stats (relative to current zone)
        LocalDate today = LocalDate.now(zone);
        int scheduledToday = 0;
        int doneToday = 0;

        for (Habit h : orderedHabits) {
            List<WeeklyHabitSummaryResponse.DayStatus> days = new ArrayList<>();
            int scheduled = 0;
            int done = 0;

            for (int i = 0; i < 7; i++) {
                LocalDate d = weekStart.plusDays(i);

                boolean isScheduled = isScheduledOn(h, d);
                HabitLogStatus st = statusByHabitByDate
                        .getOrDefault(h.getId(), Map.of())
                        .get(d);

                if (isScheduled) scheduled++;
                if (isScheduled && st == HabitLogStatus.DONE) done++;

                // today-only counters (only if "today" is within this computed week)
                if (!today.isBefore(weekStart) && !today.isAfter(weekEnd) && d.equals(today)) {
                    if (isScheduled) scheduledToday++;
                    if (isScheduled && st == HabitLogStatus.DONE) doneToday++;
                }

                days.add(WeeklyHabitSummaryResponse.DayStatus.builder()
                        .date(d)
                        .scheduled(isScheduled)
                        .status(st == null ? null : st.name())
                        .build());
            }

            totalScheduled += scheduled;
            totalDone += done;

            breakdowns.add(WeeklyHabitSummaryResponse.HabitBreakdown.builder()
                    .habitId(h.getId())
                    .title(h.getTitle())
                    .habitType(h.getHabitType() == null ? null : h.getHabitType().name())
                    .scheduled(scheduled)
                    .done(done)
                    .completionRate(scheduled == 0 ? 0.0 : round2((double) done / scheduled))
                    .days(days)
                    .build());
        }

        // perfect days
        int perfectDays = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate d = weekStart.plusDays(i);

            boolean anyScheduledThatDay = false;
            boolean allDoneThatDay = true;

            for (Habit h : orderedHabits) {
                boolean scheduled = isScheduledOn(h, d);
                if (!scheduled) continue;

                anyScheduledThatDay = true;
                HabitLogStatus st = statusByHabitByDate.getOrDefault(h.getId(), Map.of()).get(d);
                if (st != HabitLogStatus.DONE) {
                    allDoneThatDay = false;
                    break;
                }
            }

            if (anyScheduledThatDay && allDoneThatDay) perfectDays++;
        }

        // streak ending today (only if today lies in this week window)
        int currentStreak = 0;
        if (!today.isBefore(weekStart) && !today.isAfter(weekEnd)) {
            LocalDate cursor = today;
            while (!cursor.isBefore(weekStart)) {
                boolean anyScheduled = false;
                boolean allDone = true;

                for (Habit h : orderedHabits) {
                    boolean scheduled = isScheduledOn(h, cursor);
                    if (!scheduled) continue;

                    anyScheduled = true;
                    HabitLogStatus st = statusByHabitByDate.getOrDefault(h.getId(), Map.of()).get(cursor);
                    if (st != HabitLogStatus.DONE) {
                        allDone = false;
                        break;
                    }
                }

                if (anyScheduled && allDone) {
                    currentStreak++;
                    cursor = cursor.minusDays(1);
                } else {
                    break;
                }
            }
        }

        double completionRate = (totalScheduled == 0) ? 0.0 : ((double) totalDone / totalScheduled);

        // top 3 habits (by completionRate desc, then scheduled desc)
        List<WeeklyHabitSummaryResponse.TopHabit> topHabits = breakdowns.stream()
                .map(b -> WeeklyHabitSummaryResponse.TopHabit.builder()
                        .habitId(b.getHabitId())
                        .title(b.getTitle())
                        .scheduled(b.getScheduled())
                        .done(b.getDone())
                        .completionRate(b.getCompletionRate())
                        .build())
                .sorted(Comparator
                        .comparing(WeeklyHabitSummaryResponse.TopHabit::getCompletionRate).reversed()
                        .thenComparing(WeeklyHabitSummaryResponse.TopHabit::getScheduled, Comparator.reverseOrder())
                )
                .limit(3)
                .toList();

        return new WeekStats(
                totalScheduled, totalDone, completionRate,
                perfectDays, currentStreak,
                scheduledToday, doneToday,
                topHabits, breakdowns
        );
    }

    private ZoneId resolveUserZone(String userId) {
        try {
            var profile = userProfileRepository.findByUserId(userId).orElse(null);
            if (profile != null && profile.getTz() != null && !profile.getTz().isBlank()) {
                return ZoneId.of(profile.getTz());
            }
        } catch (Exception ignored) {}
        return ZoneId.of("Asia/Kolkata");
    }

    private boolean isScheduledOn(Habit h, LocalDate date) {
        HabitFrequency freq = h.getFrequency();
        if (freq == null) return true;

        if (freq == HabitFrequency.DAILY) return true;

        if (freq == HabitFrequency.WEEKLY) {
            Integer[] dows = h.getDaysOfWeek();
            if (dows == null || dows.length == 0) return true; // treat as every day if not specified
            int today = date.getDayOfWeek().getValue(); // Mon=1..Sun=7
            for (Integer v : dows) {
                if (v != null && v == today) return true;
            }
            return false;
        }

        // fallback: treat unknown as scheduled daily
        return true;
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private record WeekStats(
            int totalScheduled,
            int totalDone,
            double completionRate,
            int perfectDays,
            int currentStreakDays,
            int scheduledToday,
            int doneToday,
            List<WeeklyHabitSummaryResponse.TopHabit> topHabits,
            List<WeeklyHabitSummaryResponse.HabitBreakdown> breakdowns
    ) {}
}
