package com.eventhub.reservations_service.controller;

import com.eventhub.reservations_service.model.Reservation;
import com.eventhub.reservations_service.service.ReservationService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor; 


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eventhub.reservations_service.dto.ReservationResponse;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<Reservation> getAll() {
        return reservationService.getAllReservations();
    }

    @PostMapping
    public Reservation create(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }
    
    @GetMapping("/{id}")
    public ReservationResponse getReservation(@PathVariable Long id) {
    return reservationService.getReservationDetails(id);
    }

    @PutMapping("/{id}")
    public Reservation update(@PathVariable Long id, @RequestBody Reservation reservation) {
        return reservationService.updateReservation(id, reservation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

     @GetMapping("/test")
    public String test() {
        return "RESERVATIONS OK";
    }

    @GetMapping("/test-rest")
    public ResponseEntity<String> testRest(@RequestHeader(value = "Authorization", required = false) String auth) {
        System.out.println("test-rest incoming Authorization header: " + auth);
        return ResponseEntity.ok("test-rest OK");
    }

   
}
