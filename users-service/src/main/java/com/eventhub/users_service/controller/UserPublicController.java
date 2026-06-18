package com.eventhub.users_service.controller;

import com.eventhub.users_service.model.User;
import com.eventhub.users_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // <-- import manquant ajouté

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserPublicController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsersPublic() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id) {
        try {
            User user = userService.getUserByKeycloakId(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}