package com.momsbud.backend.habits.service;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public interface HabitAssignmentRefreshService {

    RefreshResult refreshForUser(String userId, String lob);

    @Data
    @Builder
    class RefreshResult {
        private String userId;
        private String lob;
        private int rulesLoaded;
        private int rulesMatched;
        private List<String> matchedRuleIds;
        private long activeAssignmentsAfterRefresh;
    }
}
