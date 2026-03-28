package se.project.domain;
import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxParticipants;
    private int currentParticipants; 
    private boolean isBooked;
    private String type;

    public Appointment(int id, LocalDateTime startTime, LocalDateTime endTime, int maxParticipants, boolean isBooked, String type) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxParticipants = maxParticipants;
        this.isBooked = isBooked;
        this.type = type; 
        this.currentParticipants = 0;
    }

    public int getId() { return id; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getMaxParticipants() { return maxParticipants; }
    public int getCurrentParticipants() { return currentParticipants; }
    public boolean isBooked() { return isBooked; }
    public String getType() { return type; }

    public void setBooked(boolean booked) { isBooked = booked; }
    public void addParticipant() { this.currentParticipants++; }
    
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}