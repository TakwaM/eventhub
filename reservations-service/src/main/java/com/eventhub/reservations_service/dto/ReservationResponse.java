package com.eventhub.reservations_service.dto;

import java.time.Instant;

public class ReservationResponse {
    private Long id;
    private Long eventId;
    private String userId;
    private Integer seatsReserved;
    private Instant createdAt;
    private String eventTitle;
    private String userName;


    public ReservationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Integer getSeatsReserved() { return seatsReserved; }
    public void setSeatsReserved(Integer seatsReserved) { this.seatsReserved = seatsReserved; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

}