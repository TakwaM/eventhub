package com.eventhub.reservations_service.dto;

import lombok.Data;

@Data
public class ReservationResponse {
    private Long id;
    private Integer seatsReserved;
    private UserDTO user;
    private EventDTO event;
}
