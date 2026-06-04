package com.eventhub.reservations_service.service;

import com.eventhub.reservations_service.model.Reservation;
import com.eventhub.reservations_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;   // ← AJOUT OBLIGATOIRE
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
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
}
