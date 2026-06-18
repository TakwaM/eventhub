package com.eventhub.reservations_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuration Feign globale.
 * - Propague Authorization et X-User-Id depuis la requête entrante vers les appels Feign.
 * - Utilise SLF4J pour le logging (niveau DEBUG).
 */
@Configuration
public class FeignClientConfig {

    private static final Logger log = LoggerFactory.getLogger(FeignClientConfig.class);

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs == null) {
                    log.debug("Feign interceptor: RequestContextHolder is null (no incoming HTTP request)");
                    return;
                }

                try {
                    String auth = attrs.getRequest().getHeader("Authorization");
                    if (auth != null && !auth.isBlank()) {
                        template.header("Authorization", auth);
                        log.debug("Feign interceptor: Authorization header propagated");
                    } else {
                        log.debug("Feign interceptor: Authorization header not present");
                    }

                    String xUserId = attrs.getRequest().getHeader("X-User-Id");
                    if (xUserId != null && !xUserId.isBlank()) {
                        template.header("X-User-Id", xUserId);
                        log.debug("Feign interceptor: X-User-Id header propagated");
                    }

                    // Propager d'autres headers si nécessaire, par exemple trace-id
                    String traceId = attrs.getRequest().getHeader("X-B3-TraceId");
                    if (traceId != null && !traceId.isBlank()) {
                        template.header("X-B3-TraceId", traceId);
                    }
                } catch (Exception e) {
                    log.warn("Feign interceptor: failed to propagate headers", e);
                }
            }
        };
    }
}