package com.momsbud.backend.rules.engine;

import com.momsbud.backend.rules.model.RuleCondition;

import java.util.Map;

public final class ConditionEvaluator {

    private ConditionEvaluator() {}

    public static boolean evaluate(Map<String, Object> metadata, RuleCondition condition) {
        try {
            Object actual = PathResolver.resolve(metadata, condition.getPath());
            if (actual == null) return false;

            NormalizedValue left = Normalizer.normalize(actual);
            NormalizedValue right = Normalizer.normalize(condition.getValue());

            return Operators.apply(condition.getOp(), left, right);
        } catch (Exception e) {
            return false; // fail-closed
        }
    }
}
