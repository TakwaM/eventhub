package com.eventhub.reservations_service.dto;

import lombok.Data;

@Data
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String date;
    private String type;
}
