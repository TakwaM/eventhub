package com.eventhub.reservations_service.dto;

import lombok.Data;

@Data
public class NotificationMessage {
    private String userId;
    private String message;
}
