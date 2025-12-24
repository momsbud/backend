package com.momsbud.backend.rules.model;

import lombok.Data;

@Data
public class RuleCondition {
    private String path;   // $.Maternity.gestationWeek
    private String op;     // eq, between, contains
    private Object value;  // rule value
}
