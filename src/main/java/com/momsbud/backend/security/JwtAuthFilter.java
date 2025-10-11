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

        // Missing bearer → don't block; app chain will 401 for protected routes
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7).trim();
        try {
            Jws<Claims> jws = jwt.parse(token); // validates signature, issuer, exp
            Claims c = jws.getPayload();

            String userId = c.getSubject();
            String userType = String.valueOf(c.get("ut")); // e.g., CUSTOMER, DOCTOR

            var auth = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + userType))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(req, res);

        } catch (Exception e) {
            // Bad token → 401, not 403
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Invalid or expired token\"}");
        }
    }
}
