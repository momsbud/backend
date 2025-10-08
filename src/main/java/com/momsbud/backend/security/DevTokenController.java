package com.momsbud.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Profile("default") // only active in your current dev profile
@RestController
@RequestMapping("/auth/dev")
public class DevTokenController {

    private final JwtService jwt;
    private final long ttlMinutes;

    public DevTokenController(JwtService jwt, @Value("${security.jwt.dev-ttl-minutes:120}") long ttlMinutes) {
        this.jwt = jwt;
        this.ttlMinutes = ttlMinutes;
    }

    @GetMapping("/token")
    public ResponseEntity<TokenResponse> token(
            @RequestParam String userId,
            @RequestParam(defaultValue = "CUSTOMER") String userType
    ) {
        String jti = UUID.randomUUID().toString();
        String token = jwt.issue(userId, userType, jti, ttlMinutes);
        return ResponseEntity.ok(new TokenResponse("Bearer", token));
    }

    public record TokenResponse(String tokenType, String accessToken) {}
}
