package com.momsbud.backend.coreidentity.repo;

import com.momsbud.backend.coreidentity.model.OtpAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface OtpAttemptRepository extends JpaRepository<OtpAttempt, String> {
    Optional<OtpAttempt> findTopByPhoneOrderByCreatedAtDesc(String phone);
    
    // Rate limiting query methods
    long countByIpAndCreatedAtAfter(String ip, OffsetDateTime after);
    long countByPhoneAndCreatedAtAfter(String phone, OffsetDateTime after);
}
