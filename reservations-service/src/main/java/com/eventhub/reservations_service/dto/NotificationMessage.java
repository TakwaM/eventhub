package com.eventhub.reservations_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor   // 🔥 constructeur avec arguments
@NoArgsConstructor    // 🔥 constructeur vide (obligatoire pour RabbitMQ)
public class NotificationMessage {
    private String userId;
    private String message;
}
