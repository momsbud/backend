package com.momsbud.backend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true) // enables @PermitAll, @RolesAllowed, @DenyAll
public class MethodSecurityConfig { }
