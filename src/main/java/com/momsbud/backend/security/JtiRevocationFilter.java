package com.momsbud.backend.security;

import com.momsbud.backend.coreidentity.repo.UserSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class JtiRevocationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserSessionRepository sessionRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Only check if previously authenticated by JWT filter
        if (auth != null && auth.isAuthenticated()) {
            String bearer = req.getHeader("Authorization");
            if (bearer != null && bearer.startsWith("Bearer ")) {
                String token = bearer.substring(7).trim();
                String jtiStr = jwtService.extractJti(token);

                if (jtiStr != null && !jtiStr.isBlank()) {
                    UUID jti;
                    try {
                        jti = UUID.fromString(jtiStr);
                    } catch (IllegalArgumentException ex) {
                        // Malformed jti – treat as unauthorized session
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("application/json");
                        res.getWriter().write("{\"error\":\"Session invalid\"}");
                        return;
                    }

                    // If session is revoked -> 401
                    if (sessionRepo.existsByJtiAndRevokedTrue(jti)) {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("application/json");
                        res.getWriter().write("{\"error\":\"Session revoked\"}");
                        return;
                    }

                    // Optional: DB-side expiry check if you store expiresAt
                    if (sessionRepo.isExpired(jti, OffsetDateTime.now())) {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("application/json");
                        res.getWriter().write("{\"error\":\"Session expired\"}");
                        return;
                    }
                }
            }
        }

        chain.doFilter(req, res);
    }
}
