package com.eventhub.events_service.controller;

import com.eventhub.events_service.model.Event;
import com.eventhub.events_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.eventhub.events_service.repository.EventRepository;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;

    @GetMapping
    public List<Event> getAll() {
        return eventService.getAllEvents();
    }

    @PostMapping
    public Event create(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @GetMapping("/{id}")
    public Event getById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    // ✔ CORRECT
    @PostMapping("/{id}/reserve")
    public ResponseEntity<?> reserve(@PathVariable Long id) {
        return eventService.reserveSeat(id);
    }
    @PostMapping("/{id}/decrement")
    public ResponseEntity<Void> decrementSeats(
        @PathVariable Long id,
        @RequestParam(defaultValue = "1") Integer seats) {

    Event event = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));

    if (event.getAvailableSeats() >= seats) {
        event.setAvailableSeats(event.getAvailableSeats() - seats);
        eventRepository.save(event);
    }

    return ResponseEntity.ok().build();
}
@PostMapping("/{id}/increment")
public ResponseEntity<Void> incrementSeats(
        @PathVariable Long id,
        @RequestParam int seats
) {
    Event event = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));

    event.setAvailableSeats(event.getAvailableSeats() + seats);
    eventRepository.save(event);

    return ResponseEntity.ok().build();
}


}