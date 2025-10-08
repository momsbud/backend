package com.momsbud.backend.coreidentity.repo;

import com.momsbud.backend.coreidentity.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, String> {
    Optional<DoctorProfile> findByUserId(String userId);
}
