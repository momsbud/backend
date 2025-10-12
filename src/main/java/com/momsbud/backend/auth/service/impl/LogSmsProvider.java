package com.momsbud.backend.auth.service.impl;

import com.momsbud.backend.auth.config.SmsConfig;
import com.momsbud.backend.auth.service.SmsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Development-only SMS provider that logs OTP codes instead of sending them.
 */
@Slf4j
@Service("logSmsProvider")
@RequiredArgsConstructor
public class LogSmsProvider implements SmsProvider {

    private final SmsConfig config;

    @Override
    public boolean sendOtp(String phone, String code, long ttlSeconds) {
        if (!config.getLog().isEnabled()) {
            log.warn("Log SMS provider is disabled. OTP not logged.");
            return false;
        }

        String template = config.getLog().getMessageTemplate();
        String message = template
                .replace("{code}", code)
                .replace("{ttl}", String.valueOf(ttlSeconds / 60));

        log.info("SMS to {}: {}", phone, message);
        return true;
    }
}