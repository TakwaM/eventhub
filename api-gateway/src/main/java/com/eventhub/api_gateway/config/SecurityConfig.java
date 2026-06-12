package com.eventhub.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import reactor.core.publisher.Mono;

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
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/**").permitAll()

                        .pathMatchers("/users-service/me/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .pathMatchers("/users-service/admin/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/users-service/**").hasAuthority("ROLE_ADMIN")

                        .pathMatchers("/events-service/admin/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/events-service/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        .pathMatchers("/reservations-service/my/**").hasAuthority("ROLE_USER")
                        .pathMatchers("/reservations-service/admin/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/reservations-service/**").hasAuthority("ROLE_ADMIN")

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
}