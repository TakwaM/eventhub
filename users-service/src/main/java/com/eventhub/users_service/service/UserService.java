package com.eventhub.users_service.service;

import com.eventhub.users_service.dto.UserAdminDTO;
import com.eventhub.users_service.model.User;
import com.eventhub.users_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    // ---------------------------
    // CRUD ADMIN
    // ---------------------------

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found for Keycloak ID: " + keycloakId
                ));
    }

    public User createUser(User user) {
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        return userRepository.save(user);
    }

    public User updateUser(String keycloakId, User updated) {
        User existing = getUserByKeycloakId(keycloakId);

        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());

        if (updated.getRole() != null) {
            existing.setRole(updated.getRole());
        }

        return userRepository.save(existing);
    }

    public void deleteUser(String keycloakId) {
        User existing = getUserByKeycloakId(keycloakId);
        userRepository.delete(existing);
    }

    // ---------------------------
    // /me + synchro Keycloak
    // ---------------------------

    public User syncUserFromToken(Jwt jwt) {

        String keycloakId = jwt.getSubject();
        String username = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setKeycloakId(keycloakId);
                    u.setUsername(username);
                    u.setEmail(email);
                    u.setRole("USER");
                    return userRepository.save(u);
                });
    }

    // ---------------------------
    // ADMIN : USERS + STATS
    // ---------------------------

    public List<UserAdminDTO> getAllUsersWithStats() {

        List<User> users = userRepository.findAll();
        List<UserAdminDTO> result = new ArrayList<>();

        for (User u : users) {

            String url = "http://analytics-service:4000/analytics/stats/users/" + u.getKeycloakId();

            Map<String, Object> stats = null;

            try {
                stats = restTemplate.getForObject(url, Map.class);
            } catch (Exception e) {
                System.out.println("⚠ Impossible de récupérer les stats pour user " + u.getKeycloakId());
            }

            int reserved = 0;
            int cancelled = 0;

            if (stats != null) {
                reserved = ((Number) stats.getOrDefault("reserved", 0)).intValue();
                cancelled = ((Number) stats.getOrDefault("cancelled", 0)).intValue();
            }

            result.add(new UserAdminDTO(
                    u.getId(),
                    u.getKeycloakId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole(),
                    reserved,
                    cancelled
            ));
        }

        return result;
    }
    
}