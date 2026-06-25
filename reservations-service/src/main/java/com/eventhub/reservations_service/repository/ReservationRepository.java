package com.eventhub.reservations_service.repository;

import com.eventhub.reservations_service.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(String userId);
    List<Reservation> findByEventId(Long eventId);
    Optional<Reservation> findByEventIdAndUserId(Long eventId, String userId);
    boolean existsByEventIdAndUserId(Long eventId, String userId);
    
}