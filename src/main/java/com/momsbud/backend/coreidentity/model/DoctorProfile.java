package com.momsbud.backend.coreidentity.model;

import com.momsbud.backend.shared.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "doctor_profile")
public class DoctorProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true, length = 26)
    private String userId; // doctor’s User.id

    @Column(name = "registration_no")
    private String registrationNo;

    @Column
    private String speciality;

    @Column(name = "clinic_org_id", length = 26)
    private String clinicOrgId;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "about_md")
    private String aboutMd;
}
