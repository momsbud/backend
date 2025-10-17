package com.momsbud.backend.referrals.repo;

import com.momsbud.backend.referrals.model.ReferralAttribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralAttributionRepository extends JpaRepository<ReferralAttribution, String> {
    Optional<ReferralAttribution> findByCodeIdAndUserIdAndIsDeletedFalse(String codeId, String userId);
    long countByCodeIdAndIsDeletedFalse(String codeId);
}
