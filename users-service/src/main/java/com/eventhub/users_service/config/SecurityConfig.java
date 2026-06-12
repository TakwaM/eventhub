package com.eventhub.users_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Mode TEST : autorise toutes les requêtes (PERMIT ALL).
     * Utiliser uniquement pour debug / test local. Ne pas laisser en production.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        // Ne pas configurer oauth2ResourceServer ici (test simple)
        return http.build();
    }
}