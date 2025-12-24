package com.momsbud.backend.rules.model;

import lombok.Data;

import java.util.List;

@Data
public class RuleMatch {
    private List<RuleCondition> all;
    private List<RuleCondition> any;
    private List<RuleCondition> not;
}
