package com.momsbud.backend.coreidentity.repo;

import com.momsbud.backend.coreidentity.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {

    // jti is a UUID column in your entity → use UUID in method signatures
    Optional<UserSession> findByJti(UUID jti);

    boolean existsByJti(UUID jti);

    // Used by JTI revocation checks
    boolean existsByJtiAndRevokedTrue(UUID jti);

    // If you prefer the active-session check instead of a revoked flag:
    boolean existsByJtiAndRevokedAtIsNull(UUID jti);

    // Optional DB-side expiry helper (if you store expiresAt)
    @Query("""
           select (count(us) > 0)
           from UserSession us
           where us.jti = :jti and us.expiresAt is not null and us.expiresAt < :now
           """)
    boolean isExpired(UUID jti, OffsetDateTime now);

    // Bulk revoke helper for logout-all
    List<UserSession> findByUserIdAndRevokedAtIsNull(String userId);
}
