package com.momsbud.backend.rules.engine;

import com.momsbud.backend.rules.model.RuleCondition;
import com.momsbud.backend.rules.model.RuleMatch;

import java.util.List;
import java.util.Map;

public final class RuleEvaluator {

    private RuleEvaluator() {}

    public static boolean matches(Map<String, Object> metadata, RuleMatch match) {
        if (match == null) return false;

        return allPass(metadata, match.getAll())
                && anyPass(metadata, match.getAny())
                && notBlocked(metadata, match.getNot());
    }

    private static boolean allPass(Map<String, Object> md, List<RuleCondition> all) {
        if (all == null || all.isEmpty()) return true;
        return all.stream().allMatch(c -> ConditionEvaluator.evaluate(md, c));
    }

    private static boolean anyPass(Map<String, Object> md, List<RuleCondition> any) {
        if (any == null || any.isEmpty()) return true;
        return any.stream().anyMatch(c -> ConditionEvaluator.evaluate(md, c));
    }

    private static boolean notBlocked(Map<String, Object> md, List<RuleCondition> not) {
        if (not == null || not.isEmpty()) return true;
        return not.stream().noneMatch(c -> ConditionEvaluator.evaluate(md, c));
    }
}
