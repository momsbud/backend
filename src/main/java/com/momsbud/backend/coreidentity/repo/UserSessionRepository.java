package com.momsbud.backend.coreidentity.repo;

import com.momsbud.backend.coreidentity.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    Optional<UserSession> findByJti(UUID jti);
    boolean existsByJti(UUID jti);
    List<UserSession> findByUserIdAndRevokedAtIsNull(String userId);
    // ✅ Used by the guard: session must exist AND not be revoked
    boolean existsByJtiAndRevokedAtIsNull(UUID jti);
}
