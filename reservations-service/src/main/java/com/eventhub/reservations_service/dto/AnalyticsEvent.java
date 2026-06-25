package com.eventhub.reservations_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {
    private Long eventId;
    private String eventName;

    private String userId;
    private String userName;

    private Instant timestamp;
    private String action; // reservation_created ou reservation_cancelled
}
