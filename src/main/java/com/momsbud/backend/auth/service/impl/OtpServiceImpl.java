package com.momsbud.backend.auth.service.impl;

import com.momsbud.backend.auth.dto.OtpSendRequest;
import com.momsbud.backend.auth.dto.OtpSendResponse;
import com.momsbud.backend.auth.dto.OtpVerifyRequest;
import com.momsbud.backend.auth.dto.OtpVerifyResponse;
import com.momsbud.backend.auth.service.OtpService;
import com.momsbud.backend.auth.util.OtpUtil;
import com.momsbud.backend.coreidentity.model.OtpAttempt;
import com.momsbud.backend.coreidentity.model.OtpState;
import com.momsbud.backend.coreidentity.model.User;
import com.momsbud.backend.coreidentity.model.UserSession;
import com.momsbud.backend.coreidentity.repo.OtpAttemptRepository;
import com.momsbud.backend.coreidentity.repo.UserRepository;
import com.momsbud.backend.coreidentity.repo.UserSessionRepository;
import com.momsbud.backend.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpAttemptRepository otpRepo;
    private final UserRepository userRepo;
    private final UserSessionRepository sessionRepo;
    private final JwtService jwt;

    @Value("${app.otp.length:6}") private int otpLength;
    @Value("${app.otp.ttl-seconds:300}") private long otpTtlSeconds;
    @Value("${app.otp.max-attempts:5}") private int maxAttempts;           // reserved for future use
    @Value("${app.security.otp.pepper:}") private String otpPepper;

    @Override
    @Transactional
    public OtpSendResponse send(OtpSendRequest req, HttpServletRequest http) {
        String ip = clientIp(http);
        String phone = OtpUtil.normalizePhone(req.getPhone());

        String code = OtpUtil.numericCode(otpLength);
        String codeHash = OtpUtil.hash(code, otpPepper);

        var attempt = OtpAttempt.builder()
                .phone(phone)
                .state(OtpState.SENT)
                .expiresAt(OffsetDateTime.now().plusSeconds(otpTtlSeconds))
                .ip(ip)
                .deviceFingerprint(req.getDeviceFingerprint())
                .build();

        // Persist hashed code (field exists in your entity)
        attempt.setCodeHash(codeHash);
        otpRepo.save(attempt);

        // DEV ONLY: print OTP to logs (replace with Outbox later)
        System.out.println("[DEV][OTP] phone=" + phone + " code=" + code + " attemptId=" + attempt.getId());

        return new OtpSendResponse(attempt.getId(), otpTtlSeconds);
    }

    @Override
    @Transactional
    public OtpVerifyResponse verify(OtpVerifyRequest req, HttpServletRequest http) {
        String ip = clientIp(http);
        String phone = OtpUtil.normalizePhone(req.getPhone());

        var latest = otpRepo.findTopByPhoneOrderByCreatedAtDesc(phone)
                .orElseThrow(() -> new BadRequest("No OTP requested"));

        if (latest.getExpiresAt() == null || latest.getExpiresAt().isBefore(OffsetDateTime.now())) {
            latest.setFailReason("EXPIRED");
            otpRepo.save(latest);
            throw new BadRequest("OTP expired");
        }

        String expectedHash = latest.getCodeHash();
        if (expectedHash == null) throw new IllegalStateException("OtpAttempt.codeHash missing");

        String providedHash = OtpUtil.hash(req.getCode(), otpPepper);
        if (!providedHash.equals(expectedHash)) {
            latest.setFailReason("INVALID_CODE");
            otpRepo.save(latest);
            throw new BadRequest("Invalid OTP");
        }

        latest.setState(OtpState.VERIFIED);
        latest.setFailReason(null);
        otpRepo.save(latest);

        // Upsert user by phone
        User user = userRepo.findByPhone(phone).orElseGet(() -> {
            User u = new User();
            u.setPhone(phone);
            return userRepo.save(u);
        });

        // Create session (JTI)
        UUID jti = UUID.randomUUID();
        var session = UserSession.builder()
                .userId(user.getId())
                .jti(jti)
                .firstIp(ip)
                .lastIp(ip)
                .lastSeenAt(OffsetDateTime.now())
                .build();
        sessionRepo.save(session);

        // Issue JWT (your JwtService already supports subject=userId, id=jti, claim "ut")
        String userType = (user.getUserType() != null) ? user.getUserType().name() : "USER";
        String token = jwt.issue(user.getId(), userType, jti.toString(),1440L);

        return new OtpVerifyResponse(token, user.getId(), jti.toString());
    }

    private static String clientIp(HttpServletRequest req) {
        String fwd = req.getHeader("X-Forwarded-For");
        if (fwd != null && !fwd.isBlank()) return fwd.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    // Local exception type to keep controller clean; feel free to move to a shared error package.
    public static class BadRequest extends RuntimeException {
        public BadRequest(String msg) { super(msg); }
    }
}
