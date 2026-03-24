package se.project.domain;

import java.time.LocalDateTime;

/**
 * Represents an appointment slot in the system.
 */
public class Appointment {
    private int id;
    private LocalDateTime dateTime;
    private String type; 
    private boolean isBooked;

    // الـ Constructor الجديد ليتناسب مع الخدمة
    public Appointment(int id, LocalDateTime dateTime, boolean isBooked) {
        this.id = id;
        this.dateTime = dateTime;
        this.isBooked = isBooked;
    }

    // Getters and Setters
    public int getId() { return id; }
    public LocalDateTime getDateTime() { return dateTime; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }
}
