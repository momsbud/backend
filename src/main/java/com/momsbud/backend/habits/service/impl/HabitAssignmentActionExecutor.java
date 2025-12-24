package com.momsbud.backend.habits.service.impl;

import com.momsbud.backend.habits.model.HabitAssignmentEntity;
import com.momsbud.backend.habits.repo.HabitAssignmentRepository;
import com.momsbud.backend.habits.repo.HabitRepository;
import com.momsbud.backend.rules.model.Rule;
import com.momsbud.backend.rules.spi.ActionExecutor;
import com.momsbud.backend.shared.jpa.id.Ulids;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HabitAssignmentActionExecutor implements ActionExecutor {

    private final HabitAssignmentRepository assignmentRepo;
    private final HabitRepository habitRepo;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(String userId, Map<String, Object> userMetadata, List<Rule> matchedRules) {
        if (matchedRules == null || matchedRules.isEmpty()) return;

        for (Rule rule : matchedRules) {
            Map<String, Object> action = safeMap(rule.getAction());
            List<Map<String, Object>> assignHabits = safeListOfMaps(action.get("assignHabits"));

            Integer windowDays = safeInt(action.get("assignmentWindowDays"));
            OffsetDateTime assignedUntil = (windowDays != null && windowDays > 0)
                    ? OffsetDateTime.now(ZoneOffset.UTC).plusDays(windowDays)
                    : null;

            String ruleDedupeKey = safeString(action.get("dedupeKey"));

            for (Map<String, Object> item : assignHabits) {
                String habitId = safeString(item.get("habitId"));
                String habitTag = safeString(item.get("habitTag"));

                // If neither is provided, skip safely
                if ((habitId == null || habitId.isBlank()) && (habitTag == null || habitTag.isBlank())) {
                    continue;
                }

                if (habitId != null && !habitId.isBlank()) {
                    assignOne(userId, rule, habitId, ruleDedupeKey, assignedUntil, userMetadata);
                } else {
                    // habitTag: may resolve to multiple habits; for MVP take first or assign all
                    var habits = habitRepo.findActiveByTag(habitTag);
                    if (habits == null || habits.isEmpty()) {
                        continue;
                    }
                    // MVP choice: assign first match (keeps deterministic)
                    assignOne(userId, rule, habits.get(0).getId(), deriveDedupe(ruleDedupeKey, habitTag), assignedUntil, userMetadata);
                }
            }
        }
    }

    private void assignOne(
            String userId,
            Rule rule,
            String habitId,
            String dedupeKey,
            OffsetDateTime assignedUntil,
            Map<String, Object> userMetadata
    ) {
        // Dedupe by ACTIVE user+habit
        boolean existsByHabit = assignmentRepo
                .findFirstByUserIdAndHabitIdAndStatusAndIsDeletedFalse(userId, habitId, "ACTIVE")
                .isPresent();
        if (existsByHabit) return;

        // Dedupe by ACTIVE user+dedupeKey if provided
        if (dedupeKey != null && !dedupeKey.isBlank()) {
            boolean existsByKey = assignmentRepo
                    .findFirstByUserIdAndDedupeKeyAndStatusAndIsDeletedFalse(userId, dedupeKey, "ACTIVE")
                    .isPresent();
            if (existsByKey) return;
        }

        var now = OffsetDateTime.now(ZoneOffset.UTC);

        HabitAssignmentEntity entity = HabitAssignmentEntity.builder()
                .userId(userId)
                .habitId(habitId)
                .ruleId(rule.getId())
                .status("ACTIVE")
                .assignedFrom(now)
                .assignedUntil(assignedUntil)
                .dedupeKey(dedupeKey)
                .reason("RULE_MATCH")
                .build();

        // Put explainability into metadata (safe with BaseEntity Map<String,Object>)
        Map<String, Object> md = entity.getMetadata();
        md.put("lob", rule.getLob());
        md.put("rulePriority", rule.getPriority());
        md.put("ruleId", rule.getId());
        md.put("assignedAt", now.toString());
        md.put("profileSnapshot", safeCompactProfile(userMetadata)); // optional, small snapshot

        // ULID generation: use your existing generator (if you have a static helper) or set id yourself
        // If you already rely on @GeneratedValue(ulid) it will fill, but keeping explicit is consistent with your comment.
        entity.setId(Ulids.newUlid()); // adjust to your actual ULID utility if different

        assignmentRepo.save(entity);
    }

    private String deriveDedupe(String base, String habitTag) {
        if (base != null && !base.isBlank()) return base + ":" + habitTag;
        return "TAG:" + habitTag;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeMap(Object o) {
        if (o instanceof Map<?, ?> m) return (Map<String, Object>) m;
        if (o == null) return Map.of();
        return objectMapper.convertValue(o, Map.class);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> safeListOfMaps(Object o) {
        if (o instanceof List<?> list) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof Map<?, ?> m) out.add((Map<String, Object>) m);
                else out.add(objectMapper.convertValue(item, Map.class));
            }
            return out;
        }
        return List.of();
    }

    private Integer safeInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return null; }
    }

    private String safeString(Object o) {
        return o == null ? null : o.toString();
    }

    private Map<String, Object> safeCompactProfile(Map<String, Object> userMetadata) {
        // Keep snapshot small. Store only LOB sections; do not dump huge payloads.
        if (userMetadata == null) return Map.of();
        Map<String, Object> out = new HashMap<>();
        if (userMetadata.containsKey("Maternity")) out.put("Maternity", userMetadata.get("Maternity"));
        if (userMetadata.containsKey("Agriculture")) out.put("Agriculture", userMetadata.get("Agriculture"));
        return out;
    }
}
