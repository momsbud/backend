package com.momsbud.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;

    public JwtService(
            @Value("${app.security.jwt.secret}") String base64Secret,
            @Value("${app.security.jwt.issuer:momsbud}") String issuer
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
    }

    public String issue(String userId, String userType, String jti, long ttlMinutes) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlMinutes * 60);
        return Jwts.builder()
                .issuer(issuer)
                .subject(userId)
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(Map.of("ut", userType))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    public String extractJti(String token) {
        return parse(token).getPayload().getId();
    }

    public String extractUserId(String token) {
        return parse(token).getPayload().getSubject();
    }

    public String extractUserType(String token) {
        Object ut = parse(token).getPayload().get("ut");
        return ut != null ? ut.toString() : null;
    }
}
