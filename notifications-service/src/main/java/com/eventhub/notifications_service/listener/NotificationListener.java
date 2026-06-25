package com.eventhub.notifications_service.listener;

import com.eventhub.notifications_service.config.RabbitMQConfig;
import com.eventhub.reservations_service.dto.NotificationMessage;
import com.eventhub.notifications_service.model.Notification;
import com.eventhub.notifications_service.repository.NotificationRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleNotification(NotificationMessage message) {

    Notification notif = new Notification();
    notif.setUserId(message.getUserId());
    notif.setMessage(message.getMessage());
    notif.setRead(false);
    notif.setTimestamp(LocalDateTime.now()); // 🔥 OBLIGATOIRE

    notificationRepository.save(notif);

    System.out.println("📩 Notification reçue : " + message.getMessage());
}

@RabbitListener(queues = "reservation.cancelled.queue")
public void handleCancellation(NotificationMessage message) {
    System.out.println("📩 Notification reçue (annulation) : " + message.getMessage());

    Notification notif = new Notification();
    notif.setUserId(message.getUserId());
    notif.setMessage(message.getMessage());
    notif.setTimestamp(LocalDateTime.now());
    notif.setRead(false);

    notificationRepository.save(notif);
}

}