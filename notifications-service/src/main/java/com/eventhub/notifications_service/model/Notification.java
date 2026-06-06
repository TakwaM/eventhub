package com.eventhub.notifications_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private Long userId;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;
}
