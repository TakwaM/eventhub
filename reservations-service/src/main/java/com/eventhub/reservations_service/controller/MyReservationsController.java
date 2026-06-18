package com.eventhub.reservations_service.controller;

import com.eventhub.reservations_service.dto.ReservationResponse;
import com.eventhub.reservations_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class MyReservationsController {

    private final ReservationService reservationService;

    /**
     * Récupère les réservations de l'utilisateur courant.
     * Priorité : header X-User-Id injecté par le gateway.
     * Exemple : GET /reservations/my  avec header X-User-Id: <uuid>
     */
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> myReservations(
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestParam(value = "userId", required = false) String userIdParam) {

        String userId = (xUserId != null && !xUserId.isBlank()) ? xUserId : userIdParam;
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(List.of());
        }

        List<ReservationResponse> list = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * Optionnel : endpoint pour récupérer un résumé (count) des réservations de l'utilisateur.
     */
    @GetMapping("/my/count")
    public ResponseEntity<Map<String, Integer>> myReservationsCount(
            @RequestHeader(value = "X-User-Id", required = false) String xUserId,
            @RequestParam(value = "userId", required = false) String userIdParam) {

        String userId = (xUserId != null && !xUserId.isBlank()) ? xUserId : userIdParam;
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("count", 0));
        }

        int count = reservationService.getReservationsByUserId(userId).size();
        return ResponseEntity.ok(Map.of("count", count));
    }
}