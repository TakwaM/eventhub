package com.eventhub.events_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.eventhub.events_service.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
