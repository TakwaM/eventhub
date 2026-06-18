package com.eventhub.notifications_service.controller;

import com.eventhub.notifications_service.model.Notification;
import com.eventhub.notifications_service.repository.NotificationRepository;
import com.eventhub.notifications_service.service.EmailService;
import com.eventhub.notifications_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final NotificationService notificationService; // 🔥 maintenant injecté correctement

    // 1️⃣ Récupérer toutes les notifications d’un user
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable String userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    // 2️⃣ Nombre de notifications non lues
    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        long count = notificationRepository.countByUserIdAndReadFalse(userId);
        return ResponseEntity.ok(count);
    }

    // 3️⃣ Marquer une notification comme lue
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notif.setRead(true);
        notificationRepository.save(notif);

        return ResponseEntity.noContent().build();
    }

    // 4️⃣ Test d'envoi d'email
    @GetMapping("/test-email")
    public String testEmail() {
        emailService.sendEmail(
                "takwa.mansour@esprit.tn",
                "Test EventHub",
                "Hello poto ! Ceci est un test d'envoi d'email."
        );
        return "OK";
    }

    // 5️⃣ Créer une notification (appelée par Angular)
    @PostMapping
    public Notification createNotification(@RequestBody Notification notif) {
        return notificationService.save(notif); // 🔥 fonctionne maintenant
    }
}
