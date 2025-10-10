package com.momsbud.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final String issuer;
    private final long defaultTtlMinutes;
    private final SecretKey key;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secretBase64,
            @Value("${app.security.jwt.issuer:momsbud}") String issuer,
            @Value("${app.security.jwt.ttl-minutes:1440}") long defaultTtlMinutes
    ) {
        Assert.hasText(secretBase64, "app.security.jwt.secret is missing/blank");
        Assert.hasText(issuer, "app.security.jwt.issuer is missing/blank");

        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secretBase64);
        } catch (Exception e) {
            throw new IllegalStateException("JWT secret is not valid Base64", e);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret too short: need >= 32 bytes (256-bit) after Base64 decode");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.defaultTtlMinutes = defaultTtlMinutes;
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
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}
