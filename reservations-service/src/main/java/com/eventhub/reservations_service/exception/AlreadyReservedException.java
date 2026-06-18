// src/main/java/com/eventhub/reservations_service/exception/AlreadyReservedException.java
package com.eventhub.reservations_service.exception;

public class AlreadyReservedException extends RuntimeException {
    public AlreadyReservedException(String message) {
        super(message);
    }
}