package com.eventhub.notifications_service.dto;

import lombok.Data;

@Data
public class NotificationMessage {
    private Long userId;
    private String message;
}
