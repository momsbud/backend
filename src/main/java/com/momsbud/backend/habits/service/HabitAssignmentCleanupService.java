package com.momsbud.backend.habits.service;

import com.momsbud.backend.rules.model.Rule;

import java.util.List;
import java.util.Map;

public interface HabitAssignmentCleanupService {

    /**
     * Ends assignments whose assigned_until < now
     * @return number of assignments ended
     */
    int cleanupExpiredAssignments();

    /**
     * Ends ACTIVE assignments whose rule no longer matches current metadata
     */
    int cleanupRuleMismatchAssignments(
            String userId,
            Map<String, Object> userMetadata,
            List<Rule> currentlyMatchedRules
    );
}
