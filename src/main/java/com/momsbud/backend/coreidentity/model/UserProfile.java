package com.momsbud.backend.coreidentity.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "user_profile")
public class UserProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true, length = 26)
    private String userId; // keep as String to avoid tight coupling

    @Column(name = "full_name")
    private String fullName;

    @Column
    private LocalDate dob;

    @Column
    private String tz;     // e.g., "Asia/Kolkata"

    @Column
    private String locale; // e.g., "en-IN"
}
