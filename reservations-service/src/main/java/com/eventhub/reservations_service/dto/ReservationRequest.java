package com.eventhub.reservations_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private Long eventId;
    private String userId; // UUID string from Keycloak
    private Integer seatsReserved;
}
