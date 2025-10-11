package com.momsbud.backend.security;

import com.momsbud.backend.coreidentity.repo.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FiltersConfig {

    private final JwtService jwtService;
    private final UserSessionRepository userSessionRepository;

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService);
    }

    @Bean
    public JtiRevocationFilter jtiRevocationFilter() {
        return new JtiRevocationFilter(jwtService, userSessionRepository);
    }
}
