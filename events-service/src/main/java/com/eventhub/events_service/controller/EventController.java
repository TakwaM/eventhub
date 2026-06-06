package com.eventhub.events_service.controller;

import com.eventhub.events_service.model.Event;
import com.eventhub.events_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

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
}
