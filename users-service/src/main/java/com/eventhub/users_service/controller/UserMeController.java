package com.eventhub.users_service.controller;

import com.eventhub.users_service.model.User;
import com.eventhub.users_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller minimaliste pour /me qui décode le token Bearer directement via JwtDecoder.
 * Usage : temporaire pour debug. Ne pas laisser en production sans vérifier la chaîne de sécurité.
 */
@RestController
@RequestMapping("/")
public class UserMeController {

    private static final Logger log = LoggerFactory.getLogger(UserMeController.class);

    private final UserService userService;
    private final JwtDecoder jwtDecoder;

    public UserMeController(UserService userService, JwtDecoder jwtDecoder) {
        this.userService = userService;
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        log.debug("GET /me called. Authorization header present: {}", auth != null && auth.startsWith("Bearer "));
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("No Authorization header");
        }

        String token = auth.substring(7);
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(token);
        } catch (Exception e) {
            log.debug("Failed to decode JWT: {}", e.getMessage());
            return ResponseEntity.status(401).body("Invalid token: " + e.getMessage());
        }

        String keycloakId = jwt.getSubject();
        if (keycloakId == null) {
            log.debug("Decoded JWT but subject (sub) is null");
            return ResponseEntity.status(401).body("Invalid token: missing subject");
        }

        User user = userService.getUserByKeycloakId(keycloakId);
        if (user == null) {
            log.debug("No user found for keycloakId={}", keycloakId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }
}