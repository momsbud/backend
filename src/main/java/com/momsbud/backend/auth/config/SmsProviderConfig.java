package com.momsbud.backend.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable SMS provider properties.
 */
@Configuration
@EnableConfigurationProperties(SmsConfig.class)
public class SmsProviderConfig {
    // This class enables the SmsConfig properties
}