package com.eventhub.events_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminEventsController {

    @GetMapping("/events")
    public ResponseEntity<?> getAllEventsForAdmin() {
        // TODO: appeler EventService, retourner la liste complète
        return ResponseEntity.ok("Admin: list of events");
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        // TODO: delete event
        return ResponseEntity.ok("Event " + id + " deleted");
    }
}