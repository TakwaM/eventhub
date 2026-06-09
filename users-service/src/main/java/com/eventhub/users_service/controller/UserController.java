package com.eventhub.users_service.controller;

import com.eventhub.users_service.model.User;
import com.eventhub.users_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
    try {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    } catch (IllegalArgumentException ex) {
        return ResponseEntity.notFound().build();
      }
    }
    
    @GetMapping("/test")
    public String test() {
        return "Hello from Users-Service";
    }

}
