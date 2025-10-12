package com.momsbud.backend.auth.service.impl;

import com.momsbud.backend.auth.config.SmsConfig;
import com.momsbud.backend.auth.service.SmsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Twilio-based SMS provider implementation.
 * Note: This is a placeholder implementation. In a real application, 
 * you would add the Twilio SDK dependency and implement the actual API calls.
 */
@Slf4j
@Service("twilioSmsProvider")
@RequiredArgsConstructor
public class TwilioSmsProvider implements SmsProvider {

    private final SmsConfig config;

    @Override
    public boolean sendOtp(String phone, String code, long ttlSeconds) {
        try {
            SmsConfig.TwilioConfig twilioConfig = config.getTwilio();
            
            if (twilioConfig.getAccountSid() == null || twilioConfig.getAuthToken() == null) {
                log.error("Twilio configuration is incomplete. Missing accountSid or authToken.");
                return false;
            }
            
            String template = twilioConfig.getMessageTemplate();
            String message = template
                    .replace("{code}", code)
                    .replace("{ttl}", String.valueOf(ttlSeconds / 60));
            
            // In a real implementation, you would use the Twilio SDK here
            // Example:
            // Message twilioMessage = Message.creator(
            //     new PhoneNumber(phone),
            //     new PhoneNumber(twilioConfig.getFromNumber()),
            //     message
            // ).create();
            
            log.info("SMS would be sent to {} via Twilio", phone);
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio: {}", e.getMessage(), e);
            return false;
        }
    }
}