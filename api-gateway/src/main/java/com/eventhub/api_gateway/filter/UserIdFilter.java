package com.eventhub.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .onErrorResume(e -> Mono.empty())   // ✔️ évite les erreurs
                .flatMap(auth -> {

                    if (auth instanceof Authentication) {
                        String userId = auth.getName(); // sub du token Keycloak
                        exchange.getRequest().mutate()
                                .header("X-User-Id", userId)
                                .build();
                    }

                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange)); // ✔️ si pas authentifié, continue normalement
    }

    @Override
    public int getOrder() {
        return -1;
    }
}