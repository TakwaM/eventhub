package com.eventhub.reservations_service.controller;

import com.eventhub.reservations_service.dto.ReservationResponse;
import com.eventhub.reservations_service.model.Reservation;
import com.eventhub.reservations_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    // -------------------------
    // GET ALL (DTO)
    // -------------------------
    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations();
    }

    // -------------------------
    // COUNT
    // -------------------------
    @GetMapping("/count")
    public long countReservations() {
        return reservationService.getAllReservations().size();
    }

    // -------------------------
    // LATEST (tri sur createdAt)
    // -------------------------
    @GetMapping("/latest")
    public List<ReservationResponse> getLatestReservations(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return reservationService.getAllReservations()
                .stream()
                .sorted(Comparator.comparing(ReservationResponse::getCreatedAt).reversed())
                .limit(limit)
                .toList();
    }

    // -------------------------
    // CREATE (compatible avec TON service)
    // -------------------------
    @PostMapping
    public ReservationResponse create(
            @RequestParam Long eventId,
            @RequestParam String userId,
            @RequestParam Integer seats
    ) {
        Reservation saved = reservationService.createReservation(eventId, userId, seats);
        return reservationService.getReservationDetails(saved.getId());
    }

    // -------------------------
    // DELETE
    // -------------------------
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }
}
