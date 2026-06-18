package com.eventhub.reservations_service.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class DebugResponseFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(DebugResponseFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        logger.info("DEBUG FILTER BEFORE chain - {} {} headers: {}",
                request.getMethod(),
                request.getRequestURI(),
                Collections.list(request.getHeaderNames())
                           .stream()
                           .map(h -> h + "=" + request.getHeader(h))
                           .collect(Collectors.joining(", ")));

        chain.doFilter(wrappedRequest, wrappedResponse);

        byte[] respArray = wrappedResponse.getContentAsByteArray();
        String respBody = respArray.length > 0 ? new String(respArray, wrappedResponse.getCharacterEncoding()) : "<EMPTY>";
        logger.info("DEBUG FILTER AFTER chain - {} {} -> status={}, content-length={}, body={}",
                request.getMethod(), request.getRequestURI(), wrappedResponse.getStatus(), respArray.length, respBody);

        // Important : recopier le body vers la réponse réelle
        wrappedResponse.copyBodyToResponse();
    }
}