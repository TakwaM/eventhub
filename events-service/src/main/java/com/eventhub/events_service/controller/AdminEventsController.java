package com.eventhub.events_service.controller;

import com.eventhub.events_service.model.Event;
import com.eventhub.events_service.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class AdminEventsController {

    @Autowired
    private EventService eventService;

    // GET /admin/events
    @GetMapping
    public List<Event> getAllEventsForAdmin() {
        return eventService.getAllEvents();
    }

    // POST /admin/events
    @PostMapping
    public Event create(@RequestBody Event e) {
        return eventService.createEvent(e);
    }

    // PUT /admin/events/{id}
    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @RequestBody Event e) {
        return eventService.updateEvent(id, e);
    }

    // DELETE /admin/events/{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

   /* @GetMapping("/count")
public long countEvents() {
    return eventService.getAllEvents().size();
}
@GetMapping("/upcoming/week")
public long countEventsThisWeek() {
    return eventService.getAllEvents().stream()
            .filter(e -> e.getDate() != null && !e.getDate().isEmpty())
            .filter(e -> {
                LocalDate today = LocalDate.now();
                LocalDate eventDate = LocalDate.parse(e.getDate()); // conversion String → LocalDate
                return !eventDate.isBefore(today) && !eventDate.isAfter(today.plusDays(7));
            })
            .count();
}
    @GetMapping("/upcoming/week")
public long countEventsThisWeek() {
    return eventService.countEventsThisWeek();
}*/
    @GetMapping("/count")
public long countEvents() {
    return eventService.countEvents();
}



}
