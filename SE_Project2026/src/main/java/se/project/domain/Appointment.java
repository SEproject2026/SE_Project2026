package se.project.domain;

import java.time.LocalDateTime;

/**
 * Represents an appointment slot in the system.
 */
public class Appointment {
    private LocalDateTime dateTime;
    private String type; // Sprint 5 requirement
    private boolean isBooked;

    public Appointment(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        this.isBooked = false;
    }

    // Getters and Setters
    public LocalDateTime getDateTime() { return dateTime; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }
}
