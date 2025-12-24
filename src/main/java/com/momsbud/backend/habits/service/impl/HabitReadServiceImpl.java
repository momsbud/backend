// src/main/java/com/momsbud/backend/habits/service/impl/HabitReadServiceImpl.java
package com.momsbud.backend.habits.service.impl;

import com.momsbud.backend.habits.dto.HabitResponse;
import com.momsbud.backend.habits.model.Habit;
import com.momsbud.backend.habits.repo.HabitAssignmentRepository;
import com.momsbud.backend.habits.repo.HabitRepository;
import com.momsbud.backend.habits.service.HabitReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitReadServiceImpl implements HabitReadService {

    private final HabitAssignmentRepository assignmentRepo;
    private final HabitRepository habitRepo;

    @Override
    public List<HabitResponse> getAssignedHabits(String userId) {
        var assignments = assignmentRepo.findActiveByUserId(userId);
        if (assignments == null || assignments.isEmpty()) return List.of();

        // preserve assignment order (assigned_from desc)
        List<String> habitIdsInOrder = assignments.stream()
                .map(a -> a.getHabitId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (habitIdsInOrder.isEmpty()) return List.of();

        var habits = habitRepo.findAllByIdInAndIsDeletedFalse(habitIdsInOrder);
        if (habits == null || habits.isEmpty()) return List.of();

        Map<String, Habit> byId = habits.stream().collect(Collectors.toMap(Habit::getId, h -> h));

        List<HabitResponse> out = new ArrayList<>();
        for (String id : habitIdsInOrder) {
            Habit h = byId.get(id);
            if (h != null && h.isActive()) {
                out.add(toResponse(h));
            }
        }
        return out;
    }

    @Override
    public List<HabitResponse> getTodayAssignedHabits(String userId, String timezone) {
        List<HabitResponse> assigned = getAssignedHabits(userId);
        if (assigned.isEmpty()) return assigned;

        ZoneId zone = safeZone(timezone);
        DayOfWeek dow = ZonedDateTime.now(zone).getDayOfWeek();
        int today = dow.getValue(); // Mon=1..Sun=7

        return assigned.stream()
                .filter(r -> {
                    if (r.getFrequency() == null) return true;
                    if (!"WEEKLY".equals(r.getFrequency().name())) return true;
                    var days = r.getDaysOfWeek();
                    return days == null || days.isEmpty() || days.contains(today);
                })
                .toList();
    }

    private ZoneId safeZone(String timezone) {
        try {
            if (timezone == null || timezone.isBlank()) return ZoneId.of("Asia/Kolkata");
            return ZoneId.of(timezone);
        } catch (Exception e) {
            return ZoneId.of("Asia/Kolkata");
        }
    }

    private HabitResponse toResponse(Habit h) {
        return HabitResponse.builder()
                .id(h.getId())
                .title(h.getTitle())
                .description(h.getDescription())
                .frequency(h.getFrequency())
                .daysOfWeek(h.getDaysOfWeek() == null ? null : Arrays.asList(h.getDaysOfWeek()))
                .timeOfDay(h.getTimeOfDay())
                .timezone(h.getTimezone())
                .active(h.isActive())
                .createdAt(h.getCreatedAt())
                .habitType(h.getHabitType())
                .tags(h.getTags() == null ? List.of() : Arrays.asList(h.getTags()))
                .build();
    }
}
