package com.eventhub.users_service.config;

import com.eventhub.users_service.security.KeycloakUserSyncFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakUserSyncFilter keycloakUserSyncFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // tu peux affiner si tu veux, mais pour l’instant :
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt());

        // 🔥 Ajouter le filtre APRÈS l’authentification JWT
        http.addFilterAfter(keycloakUserSyncFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}