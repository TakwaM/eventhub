package com.eventhub.users_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID Keycloak (UUID)
    @Column(nullable = false, unique = true)
    private String keycloakId;

    private String username;
    private String email;

    // rôle interne (USER / ADMIN)
    private String role;
}
