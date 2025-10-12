package com.momsbud.backend.auth.service.impl;

import com.momsbud.backend.auth.service.AuthSessionService;
import com.momsbud.backend.coreidentity.model.UserSession;
import com.momsbud.backend.coreidentity.repo.UserSessionRepository;
import com.momsbud.backend.security.JwtService;
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
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) return;

        String token = header.substring(7).trim();
        String jtiStr = jwtService.extractJti(token);
        if (jtiStr == null || jtiStr.isBlank()) return;

        UUID jti;
        try {
            jti = UUID.fromString(jtiStr);
        } catch (IllegalArgumentException ex) {
            // Token jti is not a UUID → treat as no-op (or log if desired)
            return;
        }

        Optional<UserSession> opt = sessionRepository.findByJti(jti);
        if (opt.isEmpty()) return;

        UserSession s = opt.get();
        if (s.getRevokedAt() == null) {
            s.setRevokedAt(OffsetDateTime.now());
            sessionRepository.save(s);
        }
    }

    @Override
    @Transactional
    public void logoutAll(String userId) {
        if (userId == null || userId.isBlank()) return;
        List<UserSession> active = sessionRepository.findByUserIdAndRevokedAtIsNull(userId);
        if (active.isEmpty()) return;

        OffsetDateTime now = OffsetDateTime.now();
        for (UserSession s : active) {
            s.setRevokedAt(now);
        }
        sessionRepository.saveAll(active);
    }
}
