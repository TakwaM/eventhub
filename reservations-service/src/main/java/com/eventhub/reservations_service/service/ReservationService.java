package com.eventhub.reservations_service.service;

import com.eventhub.reservations_service.model.Reservation;
import com.eventhub.reservations_service.repository.ReservationRepository;
import com.eventhub.reservations_service.clients.UserClient;
import com.eventhub.reservations_service.clients.EventClient;
import com.eventhub.reservations_service.dto.UserDTO;
import com.eventhub.reservations_service.dto.EventDTO;
import com.eventhub.reservations_service.dto.ReservationResponse;
import com.eventhub.reservations_service.dto.NotificationMessage;
import com.eventhub.reservations_service.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final AmqpTemplate amqpTemplate;

    public Reservation createReservation(Reservation reservation) {

        // 1) Sauvegarder la réservation
        Reservation saved = reservationRepository.save(reservation);

        // 2) Construire le message de notification
        NotificationMessage msg = new NotificationMessage();
        msg.setUserId(saved.getUserId());
        msg.setMessage("Votre réservation pour l'événement " + saved.getEventId() + " est confirmée.");

        // 3) Envoyer le message à RabbitMQ
        amqpTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                msg
        );

        return saved;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
    }

    public Reservation updateReservation(Long id, Reservation newRes) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        existing.setUserId(newRes.getUserId());
        existing.setEventId(newRes.getEventId());
        existing.setSeatsReserved(newRes.getSeatsReserved());

        return reservationRepository.save(existing);
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }
        reservationRepository.deleteById(id);
    }

    public ReservationResponse getReservationDetails(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        UserDTO user = userClient.getUserById(reservation.getUserId());
        EventDTO event = eventClient.getEventById(reservation.getEventId());

        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setSeatsReserved(reservation.getSeatsReserved());
        response.setUser(user);
        response.setEvent(event);

        return response;
    }
}
