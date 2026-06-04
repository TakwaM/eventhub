package com.eventhub.users_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")   // ← OBLIGATOIRE
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
}
