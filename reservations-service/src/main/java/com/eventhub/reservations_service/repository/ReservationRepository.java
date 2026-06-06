package com.eventhub.reservations_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eventhub.reservations_service.model.Reservation;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByEventId(Long eventId);
}
