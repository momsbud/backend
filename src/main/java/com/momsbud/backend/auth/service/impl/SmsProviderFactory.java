package com.momsbud.backend.auth.service.impl;

import com.momsbud.backend.auth.config.SmsConfig;
import com.momsbud.backend.auth.service.SmsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory for creating SMS providers based on configuration.
 */
@Component
@RequiredArgsConstructor
public class SmsProviderFactory {
    
    private final SmsConfig smsConfig;
    private final Map<String, SmsProvider> smsProviders;
    
    /**
     * Gets the active SMS provider based on configuration.
     * 
     * @return The configured SMS provider
     */
    public SmsProvider getProvider() {
        String providerName = smsConfig.getProvider();
        String beanName = providerName + "SmsProvider";
        
        SmsProvider provider = smsProviders.get(beanName);
        if (provider == null) {
            throw new IllegalStateException("SMS provider not found: " + providerName);
        }
        
        return provider;
    }
}