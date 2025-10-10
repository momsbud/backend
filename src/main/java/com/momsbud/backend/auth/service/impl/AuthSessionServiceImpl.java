package com.momsbud.backend.auth.service.impl;

import com.momsbud.backend.auth.service.AuthSessionService;
import com.momsbud.backend.coreidentity.model.UserSession;
import com.momsbud.backend.coreidentity.repo.UserSessionRepository;
import com.momsbud.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthSessionServiceImpl implements AuthSessionService {

    private final JwtService jwtService;
    private final UserSessionRepository sessionRepository;

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) return;

        String token = auth.substring(7).trim();
        Jws<Claims> jws = jwtService.parse(token);           // validates signature & issuer
        String jtiStr = jws.getPayload().getId();            // JWT ID we set when issuing tokens
        if (jtiStr == null || jtiStr.isBlank()) return;

        UUID jti;
        try {
            jti = UUID.fromString(jtiStr);
        } catch (IllegalArgumentException e) {
            // Non-UUID JTI? Just ignore gracefully.
            return;
        }

        Optional<UserSession> sessionOpt = sessionRepository.findByJti(jti);
        sessionOpt.ifPresent(s -> {
            if (s.getRevokedAt() == null) {
                s.setRevokedAt(OffsetDateTime.now());
                s.setLastIp(clientIp(request));
                s.setLastSeenAt(OffsetDateTime.now());
                sessionRepository.save(s);
            }
        });
    }

    @Override
    @Transactional
    public void logoutAll(String userId) {
        // Optional bulk logout: revoke all active sessions for the user
        List<UserSession> sessions = sessionRepository.findByUserIdAndRevokedAtIsNull(userId);
        OffsetDateTime now = OffsetDateTime.now();
        for (UserSession s : sessions) {
            s.setRevokedAt(now);
        }
        sessionRepository.saveAll(sessions);
    }

    private static String clientIp(HttpServletRequest req) {
        String fwd = req.getHeader("X-Forwarded-For");
        if (fwd != null && !fwd.isBlank()) return fwd.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
