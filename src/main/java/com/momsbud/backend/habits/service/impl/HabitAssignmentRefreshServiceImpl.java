package com.momsbud.backend.habits.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momsbud.backend.coreidentity.repo.UserProfileRepository;
import com.momsbud.backend.habits.model.HabitRule;
import com.momsbud.backend.habits.repo.HabitAssignmentRepository;
import com.momsbud.backend.habits.repo.HabitRuleRepository;
import com.momsbud.backend.habits.service.HabitAssignmentCleanupService;
import com.momsbud.backend.habits.service.HabitAssignmentRefreshService;
import com.momsbud.backend.rules.engine.RuleEngine;
import com.momsbud.backend.rules.model.Rule;
import com.momsbud.backend.rules.model.RuleMatch;
import com.momsbud.backend.rules.spi.ActionExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HabitAssignmentRefreshServiceImpl implements HabitAssignmentRefreshService {

    private final UserProfileRepository userProfileRepository;
    private final HabitRuleRepository habitRuleRepository;
    private final HabitAssignmentRepository habitAssignmentRepository;
    private final HabitAssignmentCleanupService cleanupService;
    private final ObjectMapper objectMapper;

    @Qualifier("habitAssignmentActionExecutor")
    private final ActionExecutor habitAssignmentActionExecutor;

    private final RuleEngine ruleEngine = new RuleEngine();

    @Override
    public RefreshResult refreshForUser(String userId, String lob) {

        // 1️⃣ End expired assignments first (time-based cleanup)
        cleanupService.cleanupExpiredAssignments();

        // 2️⃣ Load user profile
        var profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "UserProfile not found for userId=" + userId));

        Map<String, Object> metadata = profile.getMetadata();

        // 3️⃣ Load active rules
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<HabitRule> dbRules = habitRuleRepository.findActiveForLobNow(lob, now);

        // 4️⃣ Convert to engine rules
        List<Rule> engineRules = dbRules.stream()
                .map(this::toEngineRule)
                .toList();

        // 5️⃣ Evaluate rules
        List<Rule> matched = ruleEngine.evaluate(metadata, engineRules);

        // 🔥 6️⃣ NEW: cleanup rule-mismatch assignments
        cleanupService.cleanupRuleMismatchAssignments(
                userId,
                metadata,
                matched
        );

        // 7️⃣ Assign new habits
        habitAssignmentActionExecutor.execute(userId, metadata, matched);

        // 8️⃣ Return summary
        long activeAssignments =
                habitAssignmentRepository.countByUserIdAndStatusAndIsDeletedFalse(
                        userId, "ACTIVE");

        return RefreshResult.builder()
                .userId(userId)
                .lob(lob)
                .rulesLoaded(dbRules.size())
                .rulesMatched(matched.size())
                .matchedRuleIds(matched.stream().map(Rule::getId).toList())
                .activeAssignmentsAfterRefresh(activeAssignments)
                .build();
    }

    private Rule toEngineRule(HabitRule r) {
        Rule out = new Rule();
        out.setId(r.getId());
        out.setLob(r.getLob());
        out.setPriority(r.getPriority());
        out.setActive(r.isActive());

        RuleMatch match = objectMapper.convertValue(r.getMatch(), RuleMatch.class);
        out.setMatch(match);

        out.setAction(r.getAction() == null ? Map.of() : r.getAction());
        return out;
    }
}
