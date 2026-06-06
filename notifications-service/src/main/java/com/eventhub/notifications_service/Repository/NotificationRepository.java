package com.eventhub.notifications_service.repository;

import com.eventhub.notifications_service.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserId(Long userId);
}
