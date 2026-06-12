package com.eventhub.api_gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeycloakReactiveRoleConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {

        List<String> roles = new ArrayList<>();

        try {
            List<String> direct = jwt.getClaimAsStringList("roles");
            if (direct != null) roles.addAll(direct);
        } catch (Exception ignored) {}

        try {
            Object realmAccessObj = jwt.getClaims().get("realm_access");
            if (realmAccessObj instanceof Map) {
                Map<?,?> realmAccess = (Map<?,?>) realmAccessObj;
                Object r = realmAccess.get("roles");
                if (r instanceof List) {
                    for (Object o : (List<?>) r) if (o != null) roles.add(o.toString());
                }
            }
        } catch (Exception ignored) {}

        try {
            Object resourceAccessObj = jwt.getClaims().get("resource_access");
            if (resourceAccessObj instanceof Map) {
                Map<?,?> resourceAccess = (Map<?,?>) resourceAccessObj;
                for (Object clientEntry : resourceAccess.values()) {
                    if (clientEntry instanceof Map) {
                        Object rr = ((Map<?,?>) clientEntry).get("roles");
                        if (rr instanceof List) {
                            for (Object o : (List<?>) rr) if (o != null) roles.add(o.toString());
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String r : roles) {
            if (r == null) continue;
            String normalized = r.startsWith("ROLE_") ? r : "ROLE_" + r;
            authorities.add(new SimpleGrantedAuthority(normalized));
        }

        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }
}