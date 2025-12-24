package com.momsbud.backend.rules.spi;

import com.momsbud.backend.rules.model.Rule;

import java.util.List;
import java.util.Map;

public interface ActionExecutor {
    void execute(String userId, Map<String, Object> userMetadata, List<Rule> matchedRules);
}
