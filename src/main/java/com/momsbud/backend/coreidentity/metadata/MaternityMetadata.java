package com.momsbud.backend.coreidentity.metadata;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MaternityMetadata {

    // --- Core pregnancy timeline ---
    private LocalDate lmpDate;          // Last menstrual period
    private LocalDate expectedDueDate;  // EDD (derived or explicit)
    private Integer gestationWeek;      // 1–42
    private Integer trimester;          // 1,2,3

    // --- Risk & medical context ---
    private RiskFlags riskFlags;

    // --- Preferences / context ---
    private Boolean firstPregnancy;
    private Boolean highRisk;
    private Set<String> conditions;     // e.g. anemia, thyroid

    // --- Validation helper ---
    public void normalize() {
        if (gestationWeek != null) {
            if (gestationWeek <= 12) trimester = 1;
            else if (gestationWeek <= 27) trimester = 2;
            else trimester = 3;
        }
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class RiskFlags {
        private Boolean gdm;          // gestational diabetes
        private Boolean hypertension;
        private Boolean anemia;
        private Boolean preeclampsia;
        private Boolean thyroid;
    }
}
