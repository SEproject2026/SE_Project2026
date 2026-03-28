package se.project.service;

import se.project.domain.Appointment;
import se.project.domain.User;
import se.project.notification.NotificationService;
import se.project.service.strategies.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class that manages appointment bookings, cancellations, and rules.
 * Implements Strategy Pattern for rules and Observer Pattern for notifications.
 * * @author YourName
 * @version 3.0
 */
public class AppointmentService {
    private List<Appointment> appointments;
    private List<NotificationService> observers; // Observer Pattern: List of subscribers
    private Map<String, BookingRuleStrategy> rules; // Strategy Pattern: Map of rules
    
    /**
     * Constructor initializes the service with sample data and strategies.
     */
    public AppointmentService() {
        this.appointments = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.rules = new HashMap<>();
        
        // Initialize Strategy Pattern rules
        rules.put("urgent", new UrgentRuleStrategy());
        rules.put("default", new DefaultRuleStrategy());
        
        // Sample data for Sprint 5 (Appointment Types)
        appointments.add(new Appointment(1, 
            LocalDateTime.of(2026, 4, 1, 10, 0), 
            LocalDateTime.of(2026, 4, 1, 11, 0), 5, false, "Urgent"));

        appointments.add(new Appointment(2, 
            LocalDateTime.of(2026, 4, 1, 11, 0), 
            LocalDateTime.of(2026, 4, 1, 12, 0), 5, true, "Virtual"));

        appointments.add(new Appointment(3, 
            LocalDateTime.of(2026, 4, 2, 9, 0), 
            LocalDateTime.of(2026, 4, 2, 10, 0), 5, false, "Follow-up"));
    }

    // --- Observer Pattern Methods ---

    /**
     * Adds a new notification observer (subscriber) to the system.
     * @param observer The notification service to add.
     */
    public void addObserver(NotificationService observer) {
        this.observers.add(observer);
    }

    /**
     * Notifies all registered observers with a specific message.
     * @param message The message to send.
     */
    private void notifyAllObservers(String message) {
        for (NotificationService obs : observers) {
            obs.update(message); // Standard Observer update call
        }
    }

    // --- Core Business Logic ---

    /**
     * US2.1 - Books an appointment and notifies all observers.
     * @param appointmentId ID of the appointment.
     * @return true if successful.
     */
    public boolean bookAppointment(int appointmentId) {
        for (Appointment app : appointments) {
            if (app.getId() == appointmentId && !app.isBooked()) {
                app.setBooked(true);
                notifyAllObservers("Appointment with ID " + appointmentId + " is booked!");
                return true;
            }
        }
        return false;
    }

    /**
     * US5.2 - Apply different rules per type using Strategy Pattern.
     * @param app The appointment to validate.
     * @return true if valid according to its strategy.
     */
    public boolean isValidDurationPerType(Appointment app) {
        String type = (app.getType() != null) ? app.getType().toLowerCase() : "default";
        BookingRuleStrategy strategy = rules.getOrDefault(type, rules.get("default"));
        return strategy.isValid(app);
    }

    /**
     * Sends a manual reminder to all observers.
     * @param appointmentId ID of the appointment.
     */
    public void sendAppointmentReminder(int appointmentId) {
        String message = "Reminder: Your appointment with ID " + appointmentId + " is coming up!";
        notifyAllObservers(message);
    }

    public List<Appointment> getAvailableSlots() {
        return appointments.stream()
                .filter(app -> !app.isBooked())
                .collect(Collectors.toList());
    }

    public boolean hasCapacity(Appointment app) {
        return app.getCurrentParticipants() < app.getMaxParticipants();
    }

    /**
     * US4.1 - Cancel a future appointment.
     */
    public boolean cancelAppointment(int appointmentId) {
        for (Appointment app : appointments) {
            if (app.getId() == appointmentId && app.isBooked()) {
                if (app.getStartTime().isAfter(LocalDateTime.now())) {
                    app.setBooked(false);
                    notifyAllObservers("Appointment " + appointmentId + " has been canceled.");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * US4.2 - Allows admin to cancel any appointment.
     */
    public boolean adminCancelAppointment(int appointmentId, User user) {
        if (user != null && user.isAdmin()) {
            for (Appointment app : appointments) {
                if (app.getId() == appointmentId) {
                    app.setBooked(false);
                    notifyAllObservers("Admin has canceled appointment " + appointmentId);
                    return true;
                }
            }
        }
        return false;
    }

    public List<Appointment> getAppointmentsByType(String type) {
        return appointments.stream()
                .filter(app -> app.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // Keep old duration check for backward compatibility
    public boolean isValidDuration(Appointment app) {
        long duration = java.time.Duration.between(app.getStartTime(), app.getEndTime()).toMinutes();
        return duration > 0 && duration <= 120;
    }
}