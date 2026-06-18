package com.eventhub.reservations_service.clients;

import com.eventhub.reservations_service.dto.EventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "EVENTS-SERVICE")
public interface EventClient {

    @GetMapping("/events/{id}")
    EventDTO getEventById(@PathVariable("id") Long id);
}