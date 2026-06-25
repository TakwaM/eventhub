package com.eventhub.reservations_service.service;

import com.eventhub.reservations_service.model.Reservation;
import com.eventhub.reservations_service.repository.ReservationRepository;
import com.eventhub.reservations_service.dto.ReservationResponse;
import com.eventhub.reservations_service.exception.AlreadyReservedException;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.eventhub.reservations_service.dto.NotificationMessage;
import com.eventhub.reservations_service.config.RabbitMQConfig;
import com.eventhub.reservations_service.dto.EventDTO;
import com.eventhub.reservations_service.clients.EventClient;
import com.eventhub.reservations_service.clients.UserClient;
import com.eventhub.reservations_service.service.AnalyticsPublisher;
import com.eventhub.reservations_service.dto.AnalyticsEvent;
import com.eventhub.reservations_service.dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestTemplate restTemplate; 
    private final RabbitTemplate rabbitTemplate;
    private final EventClient eventClient;
private final UserClient userClient;
private final AnalyticsPublisher analyticsPublisher;


    // -------------------------
    // CREATE RESERVATION
    // -------------------------
    @Transactional
public Reservation createReservation(Long eventId, String userId, Integer seatsReserved) {

    if (existsByEventIdAndUserId(eventId, userId)) {
        throw new AlreadyReservedException("User already reserved this event");
    }

    Reservation r = new Reservation();
    r.setEventId(eventId);
    r.setUserId(userId);
    r.setSeatsReserved(seatsReserved);

    try {
        Reservation saved = reservationRepository.save(r);

        // 🔥 Récupérer l'événement via Feign
        EventDTO event = eventClient.getEventById(eventId);

        // 🔥 Récupérer l'utilisateur via Feign
        UserDTO user = userClient.getUserById(userId);

        // 🔥 Construire l'événement Analytics enrichi
        AnalyticsEvent analyticsEvent = new AnalyticsEvent(
                saved.getEventId(),
                event.getTitle(),          // ou getName() selon ton DTO
                saved.getUserId(),
                user.getUsername(),
                saved.getCreatedAt(),
                "reservation_created"
        );

        // 🔥 Envoyer dans RabbitMQ
        analyticsPublisher.publish(analyticsEvent);

        // 🔥 Ton code existant : décrémenter les places
        decrementEventSeats(eventId, seatsReserved);

        // 🔥 Ton code existant : envoyer notification
        NotificationMessage message = new NotificationMessage(
                saved.getUserId(),
                "Votre réservation pour l'événement \"" + event.getTitle() + "\" est confirmée."
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        return saved;

    } catch (DataIntegrityViolationException ex) {
        throw new AlreadyReservedException("User already reserved this event");
    }
}



    // -------------------------
    // DECREMENT SEATS
    // -------------------------
    private void decrementEventSeats(Long eventId, Integer seats) {
        try {
            String url = "http://events-service:8081/events/" + eventId + "/decrement?seats=" + seats;
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrement seats in events-service", e);
        }
    }

    // -------------------------
    // INCREMENT SEATS (ANNULATION)
    // -------------------------
    private void incrementEventSeats(Long eventId, Integer seats) {
        try {
            String url = "http://events-service:8081/events/" + eventId + "/increment?seats=" + seats;
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to increment seats in events-service", e);
        }
    }

    // -------------------------
    // CANCEL RESERVATION
    // -------------------------
   @Transactional
public void cancelReservation(Long eventId, String userId) {

    Reservation r = reservationRepository.findByEventIdAndUserId(eventId, userId)
            .orElseThrow(() -> new RuntimeException("Reservation not found"));

    reservationRepository.delete(r);

    incrementEventSeats(eventId, r.getSeatsReserved());

    // 🔥 Récupérer infos via Feign
    EventDTO event = eventClient.getEventById(eventId);
    UserDTO user = userClient.getUserById(userId);

    // 🔥 Construire AnalyticsEvent
    AnalyticsEvent analyticsEvent = new AnalyticsEvent(
            eventId,
            event.getTitle(),
            userId,
            user.getUsername(),
            r.getCreatedAt(),
            "reservation_cancelled"
    );

    // 🔥 Envoyer dans RabbitMQ
    analyticsPublisher.publish(analyticsEvent);

    // 🔥 Ton message de notification existant
    NotificationMessage notification = new NotificationMessage(
            userId,
            "Votre réservation pour l'événement \"" + event.getTitle() + "\" a été annulée."
    );

    rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.CANCEL_ROUTING_KEY,
            notification
    );
}
    // -------------------------
    // HELPERS
    // -------------------------
    @Transactional(readOnly = true)
    public boolean hasReserved(Long eventId, String userId) {
        return reservationRepository.existsByEventIdAndUserId(eventId, userId);
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationDetails(Long id) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
        return toResponse(r);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Reservation updateReservation(Long id, Reservation updated) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));

        existing.setSeatsReserved(updated.getSeatsReserved());
        return reservationRepository.save(existing);
    }

    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    private ReservationResponse toResponse(Reservation r) {
    ReservationResponse resp = new ReservationResponse();
    resp.setId(r.getId());
    resp.setEventId(r.getEventId());
    resp.setUserId(r.getUserId());
    resp.setSeatsReserved(r.getSeatsReserved());
    resp.setCreatedAt(r.getCreatedAt());

    try {
        EventDTO event = eventClient.getEventById(r.getEventId());
        resp.setEventTitle(event.getTitle());
    } catch (Exception ignored) {}

    try {
        UserDTO user = userClient.getUserById(r.getUserId());
        resp.setUserName(user.getUsername());
    } catch (Exception ignored) {}

    return resp;
}


    public boolean existsByEventIdAndUserId(Long eventId, String userId) {
        return reservationRepository.existsByEventIdAndUserId(eventId, userId);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByUserId(String userId) {
        if (userId == null) return List.of();
        return reservationRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    private EventDTO getEvent(Long eventId) {
    String url = "http://events-service:8081/events/" + eventId;
    return restTemplate.getForObject(url, EventDTO.class);
}

@Transactional(readOnly = true)
public Reservation getReservationEntity(Long id) {
    return reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + id));
}


}
