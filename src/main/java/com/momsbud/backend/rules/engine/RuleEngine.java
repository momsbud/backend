package com.momsbud.backend.rules.engine;

import com.momsbud.backend.rules.model.Rule;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RuleEngine {

    public List<Rule> evaluate(Map<String, Object> metadata, List<Rule> rules) {
        return rules.stream()
                .filter(Rule::isActive)
                .filter(r -> RuleEvaluator.matches(metadata, r.getMatch()))
                .sorted(Comparator.comparingInt(Rule::getPriority).reversed())
                .collect(Collectors.toList());
    }
}
