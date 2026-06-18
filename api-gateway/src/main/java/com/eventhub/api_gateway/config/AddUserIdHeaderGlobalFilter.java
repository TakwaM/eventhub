package com.eventhub.api_gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AddUserIdHeaderGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -1; // avant le routage
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        return exchange.getPrincipal()
                .flatMap(principal -> {

                    if (principal instanceof JwtAuthenticationToken jwt) {
                        String userId = jwt.getToken().getSubject();

                        ServerWebExchange mutated = exchange.mutate()
                                .request(builder -> builder.header("X-User-Id", userId))
                                .build();

                        return chain.filter(mutated);
                    }

                    // IMPORTANT : laisser passer les requêtes publiques
                    return chain.filter(exchange);
                })
                // IMPORTANT : si aucun principal → exécuter quand même la requête
                .switchIfEmpty(chain.filter(exchange));
    }
}