package com.eventhub.reservations_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                String token = attributes.getRequest().getHeader("Authorization");
                System.out.println("Feign interceptor found token: " + token);
                if (token != null) {
                    template.header("Authorization", token);
                }
            } else {
                System.out.println("Feign interceptor: RequestContextHolder is null");
            }
        };
    }
}
