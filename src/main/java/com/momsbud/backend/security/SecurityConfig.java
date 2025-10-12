package com.momsbud.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JtiRevocationFilter jtiRevocationFilter;

    // Single chain — permits public endpoints and protects everything else

    // Chain 1: everything else — JWT protected
    @Bean
    @Order(0)
    SecurityFilterChain appChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        // Allow internal error dispatches and forwards without auth
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR, jakarta.servlet.DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/actuator/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Explicitly permit all methods for /auth/** to avoid method-specific mismatches
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"Forbidden\"}");
                        })
                )
                // filters — JWT first, then JTI check
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jtiRevocationFilter, JwtAuthFilter.class);

        return http.build();
    }
}
