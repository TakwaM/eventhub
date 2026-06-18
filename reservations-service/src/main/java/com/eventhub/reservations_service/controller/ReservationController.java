package com.eventhub.reservations_service.controller;

import com.eventhub.reservations_service.dto.ReservationRequest;
import com.eventhub.reservations_service.dto.ReservationResponse;
import com.eventhub.reservations_service.exception.AlreadyReservedException;
import com.eventhub.reservations_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.eventhub.reservations_service.repository.ReservationRepository;
import com.eventhub.reservations_service.model.Reservation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    /**
     * Create a reservation.
     * Priority: X-User-Id header injected by gateway overrides request.userId.
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId) {

        String userId = (xUserId != null && !xUserId.isBlank()) ? xUserId : request.getUserId();

        if (request.getEventId() == null || userId == null || request.getSeatsReserved() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventId, userId and seatsReserved are required");
        }

        // Validate UUID format for userId (si tu utilises UUID côté client)
        try {
            UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId must be a valid UUID");
        }

        // Vérification métier : si déjà réservé, on lève une exception métier
        if (reservationService.hasReserved(request.getEventId(), userId)) {
            throw new AlreadyReservedException("You already reserved this event");
        }

        var saved = reservationService.createReservation(request.getEventId(), userId, request.getSeatsReserved());
        ReservationResponse response = reservationService.getReservationDetails(saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all reservations
     * Returns a list of ReservationResponse (enriched DTOs).
     * In production protect this endpoint with roles/scopes.
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> list = reservationService.getAllReservations();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservation(@PathVariable Long id) {
        return reservationService.getReservationDetails(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody com.eventhub.reservations_service.model.Reservation reservation) {
        try {
            var updated = reservationService.updateReservation(id, reservation);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

   @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestHeader("X-User-Id") String userId) {

    Reservation r = reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    reservationService.cancelReservation(r.getEventId(), userId);

    return ResponseEntity.noContent().build();
    }


    @GetMapping("/test")
    public String test() {
        return "RESERVATIONS OK";
    }

    /**
     * Endpoint pour que le front vérifie si l'utilisateur a déjà réservé cet event.
     * Exemple d'appel : GET /reservations/check?eventId=123  avec header X-User-Id (prioritaire)
     */
    @GetMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Boolean>> check(
            @RequestParam Long eventId,
            @RequestParam(value = "userId", required = false) String userIdParam,
            @RequestHeader(value = "X-User-Id", required = false) String xUserId) {

        // Priorité au header X-User-Id (injection par gateway)
        String userId = (xUserId != null && !xUserId.isBlank()) ? xUserId : userIdParam;
        logger.info("ENTER CHECK controller - request params: eventId={}, userIdParam={}, X-User-Id header={}", eventId, userIdParam, xUserId);
        if (userId == null || userId.isBlank()) {
            logger.warn("CHECK /reservations/check called without userId (eventId={})", eventId);
            // On renvoie explicitement false plutôt que body vide
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(Collections.singletonMap("hasReserved", false));
        }

        // Optionnel : valider le format UUID si nécessaire
        try {
            UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid userId format for check: {}", userId);
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(Collections.singletonMap("hasReserved", false));
        }

        boolean has;
        try {
            // Utilise la méthode existante dans ton service (hasReserved)
            has = reservationService.hasReserved(eventId, userId);
        } catch (Exception e) {
            logger.error("Error while checking reservation for eventId={} userId={}", eventId, userId, e);
            // En cas d'erreur serveur, renvoyer 500 avec body explicite
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Collections.singletonMap("hasReserved", false));
        }

        logger.info("CHECK /reservations/check eventId={} userId={} -> hasReserved={}", eventId, userId, has);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(Collections.singletonMap("hasReserved", has));
    }
    /*@DeleteMapping
    public ResponseEntity<Void> cancel(
    @RequestParam Long eventId,
    @RequestParam String userId
    ) {
    reservationService.cancelReservation(eventId, userId);
    return ResponseEntity.noContent().build();
    }
    */
}