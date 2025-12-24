package com.momsbud.backend.habits.service.impl;

import com.momsbud.backend.habits.model.HabitAssignmentEntity;
import com.momsbud.backend.habits.repo.HabitAssignmentRepository;
import com.momsbud.backend.habits.service.HabitAssignmentCleanupService;
import com.momsbud.backend.rules.model.Rule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabitAssignmentCleanupServiceImpl implements HabitAssignmentCleanupService {

    private final HabitAssignmentRepository assignmentRepository;

    @Override
    public int cleanupExpiredAssignments() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        int ended = assignmentRepository.endExpiredAssignments(now);

        if (ended > 0) {
            log.info("HabitAssignmentCleanup: ended {} expired assignments", ended);
        }
        return ended;
    }

    @Override
    public int cleanupRuleMismatchAssignments(
            String userId,
            Map<String, Object> userMetadata,
            List<Rule> currentlyMatchedRules
    ) {
        if (currentlyMatchedRules == null) currentlyMatchedRules = List.of();

        Set<String> activeRuleIds = currentlyMatchedRules.stream()
                .map(Rule::getId)
                .collect(Collectors.toSet());

        List<HabitAssignmentEntity> activeAssignments =
                assignmentRepository.findActiveRuleAssignments(userId);

        int ended = 0;
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        for (HabitAssignmentEntity assignment : activeAssignments) {
            if (!activeRuleIds.contains(assignment.getRuleId())) {
                assignment.setStatus("ENDED");
                assignment.setUpdatedAt(now);

                assignment.getMetadata().put("endedReason", "RULE_MISMATCH");
                assignment.getMetadata().put("endedAt", now.toString());

                assignmentRepository.save(assignment);
                ended++;
            }
        }

        if (ended > 0) {
            log.info("Rule-mismatch cleanup: ended {} assignments for user {}", ended, userId);
        }
        return ended;
    }
}
