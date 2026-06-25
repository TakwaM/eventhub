package com.eventhub.notifications_service.service;

import com.eventhub.notifications_service.model.Notification;
import com.eventhub.notifications_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;
    private final EmailService emailService;

    public Notification save(Notification notif) {
        notif.setTimestamp(LocalDateTime.now());
        Notification saved = repo.save(notif);

        // 🔥 Envoi d’e-mail automatique
        if (notif.getUserEmail() != null) {
            emailService.sendEmail(
                notif.getUserEmail(),
                "Notification EventHub",
                notif.getMessage()
            );
        }

        return saved;
    }

    public List<Notification> getByUserId(String userId) {
        return repo.findByUserIdOrderByTimestampDesc(userId);
    }

    public long countUnread(String userId) {
        return repo.countByUserIdAndReadFalse(userId);
    }

    public Notification markAsRead(String id) {
        Notification notif = repo.findById(id).orElseThrow();
        notif.setRead(true);
        return repo.save(notif);
    }
}