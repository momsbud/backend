package com.momsbud.backend.auth.service;

/**
 * Interface for SMS providers to send OTP codes.
 * Implementations should be configured via application.yml.
 */
public interface SmsProvider {
    
    /**
     * Sends an OTP code to the specified phone number.
     * 
     * @param phone The phone number to send the OTP to (normalized format)
     * @param code The OTP code to send
     * @param ttlSeconds Time-to-live in seconds for the OTP
     * @return true if the message was sent successfully, false otherwise
     */
    boolean sendOtp(String phone, String code, long ttlSeconds);
}