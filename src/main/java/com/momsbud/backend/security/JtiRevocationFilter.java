package com.momsbud.backend.security;

import com.momsbud.backend.coreidentity.repo.UserSessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Blocks requests whose JWT's JTI is revoked (or missing).
 * Runs AFTER JwtAuthFilter. Skips public endpoints.
 */
@RequiredArgsConstructor
public class JtiRevocationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserSessionRepository sessionRepo;

    // Public/ignored paths (keep in sync with SecurityConfig permitAll)
    private static final List<String> SKIP = List.of(
            "/auth/**",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    private static final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pat : SKIP) {
            if (matcher.match(pat, path)) return true;
        }
        // Also skip if there is no Authorization header; JwtAuthFilter will handle 401s
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        return auth == null || !auth.startsWith("Bearer ");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws ServletException, IOException {

        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        String token = auth.substring("Bearer ".length()).trim();

        UUID jti = null;
        try {
            // Parse again here (cheap) to extract JTI reliably
            Jws<Claims> jws = jwtService.parse(token);
            String jtiStr = jws.getPayload().getId();
            if (jtiStr != null && !jtiStr.isBlank()) jti = UUID.fromString(jtiStr);
        } catch (Exception ignored) {
            // If parse fails, JwtAuthFilter would have already prevented auth.
            // Fall through to default 401 by not short-circuiting here.
        }

        if (jti == null || !sessionRepo.existsByJtiAndRevokedAtIsNull(jti)) {
            // Block: either no session found or it’s revoked
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            byte[] body = """
                    {"code":"session_revoked","message":"Your session is invalid or revoked. Please login again."}
                    """.getBytes(StandardCharsets.UTF_8);
            res.getOutputStream().write(body);
            return;
        }

        chain.doFilter(req, res);
    }
}
