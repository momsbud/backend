package com.momsbud.backend.referrals.repo;

import com.momsbud.backend.referrals.model.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReferralCodeRepository extends JpaRepository<ReferralCode, String> {
    Optional<ReferralCode> findByCodeAndActiveTrue(String code);
    List<ReferralCode> findAllByDoctorUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String doctorUserId);
    boolean existsByDoctorUserIdAndCodeAndIsDeletedFalse(String doctorUserId, String code);
}
