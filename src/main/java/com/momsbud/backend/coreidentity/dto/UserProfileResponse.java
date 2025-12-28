package com.momsbud.backend.coreidentity.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfileResponse {
    private String id;
    private String userId;

    private String fullName;
    private LocalDate dob;

    private String tz;
    private String locale;

    private Map<String, Object> metadata;
}
