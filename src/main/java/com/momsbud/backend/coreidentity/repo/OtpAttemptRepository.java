package com.momsbud.backend.coreidentity.repo;

import com.momsbud.backend.coreidentity.model.OtpAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpAttemptRepository extends JpaRepository<OtpAttempt, String> {
    Optional<OtpAttempt> findTopByPhoneOrderByCreatedAtDesc(String phone);
}
