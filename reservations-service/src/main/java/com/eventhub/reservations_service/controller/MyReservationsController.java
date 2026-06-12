package com.eventhub.reservations_service.controller;

import com.eventhub.reservations_service.model.Reservation;
import com.eventhub.reservations_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyReservationsController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> getMyReservations(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }
}