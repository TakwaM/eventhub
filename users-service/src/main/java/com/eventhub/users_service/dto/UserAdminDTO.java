package com.eventhub.users_service.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminDTO {

    private Long id;              // ID interne (PostgreSQL)
    private String keycloakId;    // UUID Keycloak
    private String username;      // Nom d'utilisateur
    private String email;         // Email
    private String role;          // USER / ADMIN

    private int reservedCount;    // 🔥 Nombre de réservations confirmées (analytics)
    private int cancelledCount;   // 🔥 Nombre d'annulations (analytics)
}
