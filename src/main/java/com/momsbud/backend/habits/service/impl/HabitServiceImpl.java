package com.momsbud.backend.habits.service.impl;

import com.momsbud.backend.habits.dto.*;
import com.momsbud.backend.habits.model.*;
import com.momsbud.backend.habits.repo.HabitRepository;
import com.momsbud.backend.habits.repo.UserHabitLogRepository;
import com.momsbud.backend.habits.service.HabitService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepo;
    private final UserHabitLogRepository logRepo;

    // ---------------------------------------------------------
    // CREATE HABIT (UPDATED WITH habitType + tags)
    // ---------------------------------------------------------
    @Override
    public HabitResponse create(String userId, CreateHabitRequest req) {
        Objects.requireNonNull(req.getTitle(), "title is required");

        HabitType type = (req.getHabitType() != null ? req.getHabitType() : HabitType.GENERIC);

        Habit h = Habit.builder()
                .userId(userId)
                .title(req.getTitle().trim())
                .description(req.getDescription())
                .frequency(req.getFrequency() == null ? HabitFrequency.DAILY : req.getFrequency())
                .timeOfDay(req.getTimeOfDay())
                .timezone(req.getTimezone())
                .active(true)
                // NEW
                .habitType(type)
                .build();

        // WEEKLY validation
        if (h.getFrequency() == HabitFrequency.WEEKLY) {
            List<Integer> dows = Optional.ofNullable(req.getDaysOfWeek()).orElse(List.of());
            if (dows.isEmpty())
                throw new IllegalArgumentException("daysOfWeek required for WEEKLY");
            h.setDaysOfWeek(dows.toArray(Integer[]::new));
        }

        // NEW — tags support
        if (req.getTags() != null) {
            h.setTags(req.getTags().toArray(new String[0]));
        }

        // NEW — metadata validation by habitType (optional)
        validateMetadataForType(type, h.getMetadata());

        h = habitRepo.save(h);
        return toResponse(h);
    }

    // ---------------------------------------------------------
    // LIST MINE (unchanged except templates will be filtered later)
    // ---------------------------------------------------------
    @Override
    public List<HabitResponse> listMine(String userId) {
        return habitRepo.findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // SET ACTIVE (unchanged)
    // ---------------------------------------------------------
    @Override
    public HabitResponse setActive(String userId, String habitId, boolean active) {
        Habit h = habitRepo.findByIdAndUserIdAndIsDeletedFalse(habitId, userId)
                .orElseThrow(() -> new NoSuchElementException("habit not found"));
        h.setActive(active);
        return toResponse(habitRepo.save(h));
    }

    // ---------------------------------------------------------
    // LOG (unchanged)
    // ---------------------------------------------------------
    @Override
    @Transactional
    public void log(String userId, String habitId, LogHabitRequest req) {
        Habit h = habitRepo.findByIdAndUserIdAndIsDeletedFalse(habitId, userId)
                .orElseThrow(() -> new NoSuchElementException("habit not found"));

        ZoneId zone = ZoneId.of(Optional.ofNullable(h.getTimezone()).orElse("Asia/Kolkata"));
        LocalDate today = LocalDate.now(zone);

        var existing = logRepo.findByHabitIdAndUserIdAndLogDateAndIsDeletedFalse(h.getId(), userId, today)
                .orElse(null);

        if (existing != null) {
            existing.setStatus(req.getStatus());
            existing.setNotes(req.getNotes());
            logRepo.save(existing);
            return;
        }

        UserHabitLog lh = UserHabitLog.builder()
                .habitId(h.getId())
                .userId(userId)
                .logDate(today)
                .status(req.getStatus())
                .notes(req.getNotes())
                .build();

        logRepo.save(lh);
    }

    // ---------------------------------------------------------
    // TODAY VIEW (UPDATED WITH habitType + tags)
    // ---------------------------------------------------------
    @Override
    public List<TodayHabitItem> today(String userId) {
        List<Habit> habits = habitRepo.findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId)
                .stream()
                .filter(Habit::isActive)
                .collect(Collectors.toList());

        // logs map for today
        ZoneId defaultZone = ZoneId.of("Asia/Kolkata");
        LocalDate defaultToday = LocalDate.now(defaultZone);

        var todayLogs = logRepo.findAllByUserIdAndLogDateAndIsDeletedFalse(userId, defaultToday);
        Map<String, HabitLogStatus> last = new HashMap<>();
        todayLogs.forEach(l -> last.put(l.getHabitId(), l.getStatus()));

        return habits.stream()
                .filter(h -> {
                    ZoneId z = ZoneId.of(Optional.ofNullable(h.getTimezone()).orElse("Asia/Kolkata"));
                    LocalDate d = LocalDate.now(z);
                    int dow = d.getDayOfWeek().getValue() % 7; // Mon=1..Sun=7 -> 0..6

                    if (h.getFrequency() == HabitFrequency.DAILY)
                        return true;

                    Integer[] arr = h.getDaysOfWeek();
                    if (arr == null || arr.length == 0)
                        return false;

                    for (Integer i : arr)
                        if (i != null && i == dow) return true;

                    return false;
                })
                .map(h -> TodayHabitItem.builder()
                        .id(h.getId())
                        .title(h.getTitle())
                        .frequency(h.getFrequency())
                        .daysOfWeek(h.getDaysOfWeek() == null ? null : Arrays.asList(h.getDaysOfWeek()))
                        .timeOfDay(h.getTimeOfDay())
                        .timezone(h.getTimezone())
                        .active(h.isActive())
                        .date(LocalDate.now(ZoneId.of(Optional.ofNullable(h.getTimezone()).orElse("Asia/Kolkata"))))
                        .lastStatus(last.get(h.getId()))

                        // NEW
                        .habitType(h.getHabitType())
                        .tags(h.getTags() == null ? null : Arrays.asList(h.getTags()))
                        .build())
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // toResponse (UPDATED)
    // ---------------------------------------------------------
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

                // NEW fields
                .habitType(h.getHabitType())
                .tags(h.getTags() == null ? null : Arrays.asList(h.getTags()))
                .build();
    }

    // ---------------------------------------------------------
    // VALIDATOR FOR METADATA (OPTIONAL)
    // ---------------------------------------------------------
    private void validateMetadataForType(HabitType type, Map<String, Object> metadata) {
        if (metadata == null) return;

        switch (type) {
            case YOGA -> {
                if (!metadata.containsKey("videoUrl")) {
                    throw new IllegalArgumentException("YOGA habit must include 'videoUrl' inside metadata");
                }
            }
            case FARMING -> {
                if (!metadata.containsKey("crop") || !metadata.containsKey("action")) {
                    throw new IllegalArgumentException("FARMING habit must include 'crop' and 'action' inside metadata");
                }
            }
            default -> {
                // GENERIC, NUTRITION, etc. need no forced validation (for now)
            }
        }
    }
}
