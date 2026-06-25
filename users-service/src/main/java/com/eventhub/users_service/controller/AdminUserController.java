package com.eventhub.users_service.controller;

import com.eventhub.users_service.model.User;
import com.eventhub.users_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.eventhub.users_service.dto.UserAdminDTO;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // GET ALL
    @GetMapping
    public List<UserAdminDTO> getAllUsersWithStats() {
    return userService.getAllUsersWithStats();
}

    // GET ONE
    @GetMapping("/{keycloakId}")
    public ResponseEntity<User> getUser(@PathVariable String keycloakId) {
        return ResponseEntity.ok(userService.getUserByKeycloakId(keycloakId));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    // UPDATE
    @PutMapping("/{keycloakId}")
    public ResponseEntity<User> updateUser(
            @PathVariable String keycloakId,
            @RequestBody User updated
    ) {
        return ResponseEntity.ok(userService.updateUser(keycloakId, updated));
    }

    // DELETE
    @DeleteMapping("/{keycloakId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String keycloakId) {
        userService.deleteUser(keycloakId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
public long countUsers() {
    return userService.getAllUsers().size();
}
    @GetMapping("/today")
public long countUsersToday() {
    return userService.countUsersRegisteredToday();
}

    
}
