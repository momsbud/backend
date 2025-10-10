package com.momsbud.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private static final AntPathMatcher PATH = new AntPathMatcher();

    /**
     * Completely bypass the JWT filter for public endpoints.
     * This prevents /auth/** from hitting AuthorizationFilter with an empty context and getting 403’ed.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getServletPath();
        return PATH.match("/auth/**", p)
                || PATH.match("/actuator/**", p)
                || PATH.match("/swagger-ui/**", p)
                || PATH.match("/v3/api-docs/**", p)
                || PATH.match("/swagger-ui.html", p);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);

        // No token? Don't block. Let security rules decide (protected routes will 401 later).
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7).trim();

        try {
            Jws<Claims> jws = jwt.parse(token);   // throws if invalid/expired
            Claims c = jws.getPayload();

            String userId = c.getSubject();                // sub
            String userType = String.valueOf(c.get("ut")); // your "CUSTOMER"/"DOCTOR" etc.

            var auth = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + userType)) // aligns with hasRole('CUSTOMER')
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(req, res);

        } catch (Exception e) {
            // Invalid token -> return 401 (clear signal), not 403
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Invalid or expired token\"}");
        }
    }
}
