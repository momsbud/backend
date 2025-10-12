package com.momsbud.backend.auth.controller;

import com.momsbud.backend.auth.dto.OtpSendRequest;
import com.momsbud.backend.auth.dto.OtpSendResponse;
import com.momsbud.backend.auth.dto.OtpVerifyRequest;
import com.momsbud.backend.auth.dto.OtpVerifyResponse;
import com.momsbud.backend.auth.service.AuthSessionService;
import com.momsbud.backend.auth.service.OtpService;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final AuthSessionService authSessionService;

    @PermitAll
    @PostMapping("/otp/send")
    public ResponseEntity<?> send(@RequestBody OtpSendRequest req, HttpServletRequest http) {
        try {
            return ResponseEntity.ok(otpService.send(req, http));
        } catch (Exception e) {
            String msg = (e.getMessage() != null) ? e.getMessage() : e.getClass().getSimpleName();
            return ResponseEntity.status(500).body(new Problem("internal_error", msg));
        }
    }

    record Problem(String code, String message) {}

    @PermitAll
    @PostMapping("/otp/verify")
    public ResponseEntity<OtpVerifyResponse> verify(@RequestBody OtpVerifyRequest req, HttpServletRequest http) {
        return ResponseEntity.ok(otpService.verify(req, http));
    }

    /** Revoke current token's session (based on Authorization: Bearer <jwt>) */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authSessionService.logout(request);
        return ResponseEntity.noContent().build(); // 204
    }

    /** Optional: Revoke all active sessions for the authenticated user */
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) return ResponseEntity.status(401).build();
        authSessionService.logoutAll(auth.getPrincipal().toString());
        return ResponseEntity.noContent().build();
    }
}
