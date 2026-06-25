package com.eventhub.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import reactor.core.publisher.Mono;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

// CORS imports
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> keycloakReactiveRoleConverter() {
        return new KeycloakReactiveRoleConverter();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            Converter<Jwt, Mono<AbstractAuthenticationToken>> keycloakReactiveRoleConverter) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors().and()
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeExchange(exchange -> exchange

                        // ---------------------------------------------------------
                        // CORS preflight (OPTIONS) — doit être en premier
                        // ---------------------------------------------------------
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ---------------------------------------------------------
                        // Actuator
                        // ---------------------------------------------------------
                        .pathMatchers("/actuator/**").permitAll()

                       // ---------------------------------------------------------
// RESERVATIONS-SERVICE
// ---------------------------------------------------------

// 🔥 Rendre les stats publiques pour users-service
.pathMatchers("/reservations-service/admin/reservations/stats/**").permitAll()

// Test public
.pathMatchers("/reservations-service/reservations/test").permitAll()

// Vérifier si réservé
.pathMatchers(HttpMethod.GET, "/reservations-service/reservations/check")
    .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

// Réserver
.pathMatchers(HttpMethod.POST, "/reservations-service/reservations")
    .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

// Voir SES réservations
.pathMatchers(HttpMethod.GET, "/reservations-service/reservations/my")
    .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

// Annuler une réservation
.pathMatchers(HttpMethod.DELETE, "/reservations-service/reservations/**")
    .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
.pathMatchers(HttpMethod.GET, "/reservations-service/reservations")
    .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")


// ADMIN (⚠ doit venir APRÈS la règle permitAll)
.pathMatchers("/reservations-service/admin/**")
    .hasAuthority("ROLE_ADMIN")

                        // ---------------------------------------------------------
                        // EVENTS-SERVICE (public)
                        // ---------------------------------------------------------
                        .pathMatchers("/events-service/events").permitAll()
                        .pathMatchers("/events-service/events/**").permitAll()
                        .pathMatchers("/events-service/admin/**")
                            .hasAuthority("ROLE_ADMIN")

                        // ---------------------------------------------------------
                        // USERS-SERVICE (protégé)
                        // ---------------------------------------------------------
                        .pathMatchers("/users-service/me/**")
                            .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .pathMatchers("/users-service/admin/**")
                            .hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/users-service/**")
                            .hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/analytics/**")
                            .hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/notifications-service/**").authenticated()
                        // ---------------------------------------------------------
                        // Default rule : ADMIN ONLY
                        // ---------------------------------------------------------
                        .anyExchange().access((authMono, ctx) ->
                                authMono.map(authentication ->
                                        authentication.getAuthorities().stream()
                                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                                ).map(AuthorizationDecision::new)
                        )
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakReactiveRoleConverter))
                )
                .build();
    }

    // ---------------------------------------------------------
    // CORS GLOBAL
    // ---------------------------------------------------------
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Origines autorisées (dev)
        List<String> allowedOrigins = Arrays.asList("http://localhost:4200", "http://127.0.0.1:4200");
        corsConfig.setAllowedOrigins(allowedOrigins);

        // Méthodes et headers autorisés
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Idempotency-Key"));

        // Exposer explicitement les headers nécessaires au client
        corsConfig.setExposedHeaders(Arrays.asList("Content-Type", "Content-Length", "Authorization"));

        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}