package com.momsbud.backend.rules.model;

import lombok.Data;

import java.util.Map;

@Data
public class Rule {
    private String id;
    private String lob;
    private int priority;
    private boolean active;

    private RuleMatch match;
    private Map<String, Object> action;
}
