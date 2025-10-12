package com.momsbud.backend.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for SMS providers.
 * Loaded from application.yml under app.sms namespace.
 */
@Data
@ConfigurationProperties(prefix = "app.sms")
public class SmsConfig {
    
    /**
     * The active SMS provider to use
     */
    private String provider = "log";
    
    /**
     * Configuration for Twilio SMS provider
     */
    private TwilioConfig twilio = new TwilioConfig();
    
    /**
     * Configuration for log-based SMS provider (development only)
     */
    private LogConfig log = new LogConfig();
    
    /**
     * Generic properties map for any provider-specific configuration
     */
    private Map<String, Map<String, String>> properties = new HashMap<>();
    
    /**
     * Configuration for Twilio SMS provider
     */
    @Data
    public static class TwilioConfig {
        private String accountSid;
        private String authToken;
        private String fromNumber;
        private String messageTemplate = "Your OTP code is: {code}. It expires in {ttl} minutes.";
    }
    
    /**
     * Configuration for log-based SMS provider (development only)
     */
    @Data
    public static class LogConfig {
        private String messageTemplate = "Your OTP code is: {code}. It expires in {ttl} minutes.";
        private boolean enabled = true;
    }
}