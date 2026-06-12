package com.eventhub.users_service.service;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.eventhub.users_service.model.User;
import com.eventhub.users_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // CRUD ADMIN
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User newUser) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setUsername(newUser.getUsername());
        existing.setEmail(newUser.getEmail());
        existing.setPassword(newUser.getPassword());

        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    // ---------------------------
    // LOGIQUE /me
    // ---------------------------

    public User getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found for Keycloak ID: " + keycloakId));
    }

    public User updateUserByKeycloakId(String keycloakId, User newUser) {
        User existing = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found for Keycloak ID: " + keycloakId));

        existing.setUsername(newUser.getUsername());
        existing.setEmail(newUser.getEmail());
        existing.setPassword(newUser.getPassword());

        return userRepository.save(existing);
    }
}