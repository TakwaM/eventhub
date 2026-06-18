package com.eventhub.events_service.service;

import com.eventhub.events_service.model.Event;
import com.eventhub.events_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;


import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public Event updateEvent(Long id, Event newEvent) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        existing.setTitle(newEvent.getTitle());
        existing.setDescription(newEvent.getDescription());
        existing.setDate(newEvent.getDate());
        existing.setLocation(newEvent.getLocation());

        return eventRepository.save(existing);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        eventRepository.deleteById(id);
    }
    
   public ResponseEntity<?> reserveSeat(Long id) {

    Event event = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));

    if (event.getAvailableSeats() <= 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("No seats available");
    }

    event.setAvailableSeats(event.getAvailableSeats() - 1);
    eventRepository.save(event);

     return ResponseEntity.ok("Reserved");
   }


}
