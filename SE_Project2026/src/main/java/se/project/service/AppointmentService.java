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
 * Service class that manages appointment bookings, cancellations, and business rules.
 * This class serves as the 'Subject' in the Observer Pattern for notifications
 * and the 'Context' in the Strategy Pattern for enforcing appointment rules.
 * * @author Mala, Rahaf, Raghad
 * @version 3.5
 */
public class AppointmentService {

    /** List of all appointments managed by the system. */
    private List<Appointment> appointments;

    /** * List of observers to be notified when appointment status changes. 
     * Static to ensure consistent state across tests and main execution.
     */
    private static List<NotificationService> observers = new ArrayList<>();

    /** Map containing various business rule strategies indexed by appointment type. */
    private Map<String, BookingRuleStrategy> rules;

    /**
     * Constructor that initializes the service, sets up the strategy map,
     * and populates the system with initial sample data.
     */
    public AppointmentService() {
        this.appointments = new ArrayList<>();
        this.rules = new HashMap<>();

        rules.put("urgent", new UrgentRuleStrategy());
        rules.put("default", new DefaultRuleStrategy());

        // Initial Data
        appointments.add(new Appointment(1,
                LocalDateTime.of(2026, 5, 1, 10, 0),
                LocalDateTime.of(2026, 5, 1, 11, 0), 5, false, "Urgent"));

        appointments.add(new Appointment(2,
                LocalDateTime.of(2026, 5, 1, 11, 0),
                LocalDateTime.of(2026, 5, 1, 12, 0), 5, false, "Virtual"));

        appointments.add(new Appointment(3,
                LocalDateTime.of(2026, 5, 2, 9, 0),
                LocalDateTime.of(2026, 5, 2, 10, 0), 5, false, "Follow-up"));
    }

    /**
     * US1.1/US5.1 - Adds a new appointment slot to the system.
     * Usually performed by an administrator.
     * @param app The appointment object to be added.
     */
    public void addAppointment(Appointment app) {
        this.appointments.add(app);
        System.out.println("New appointment added successfully.");
    }

    /**
     * Registers a new notification observer (subscriber) to the notification list.
     * @param observer The notification service implementation to add.
     */
    public void addObserver(NotificationService observer) {
        observers.add(observer);
    }

    /**
     * Notifies all registered observers by calling their update method with a message.
     * @param message The notification message content to be sent.
     */
    private void notifyAllObservers(String message) {
        for (NotificationService obs : observers) {
            obs.update(message);
        }
    }

    /**
     * US2.1 - Books an appointment by its unique ID.
     * If successful, triggers a notification to all observers.
     * @param appointmentId The unique identifier of the appointment.
     * @return true if the appointment was found and successfully booked; false otherwise.
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
     * US5.2 - Validates appointment duration based on its specific type using the Strategy Pattern.
     * @param app The appointment object to validate.
     * @return true if the duration complies with the specific type's rules; false otherwise.
     */
    public boolean isValidDurationPerType(Appointment app) {
        String type = (app.getType() != null) ? app.getType().toLowerCase() : "default";
        BookingRuleStrategy strategy = rules.getOrDefault(type, rules.get("default"));
        return strategy.isValid(app);
    }

    /**
     * Manually triggers a reminder notification for a specific appointment ID.
     * @param appointmentId The ID of the appointment to send a reminder for.
     */
    public void sendAppointmentReminder(int appointmentId) {
        String message = "Reminder: Your appointment with ID " + appointmentId + " is coming up!";
        notifyAllObservers(message);
    }

    /**
     * Retrieves all appointment slots that are currently not booked.
     * @return A list of available Appointment objects.
     */
    public List<Appointment> getAvailableSlots() {
        return appointments.stream()
                .filter(app -> !app.isBooked())
                .collect(Collectors.toList());
    }

    /**
     * US2.3 - Checks if an appointment has space for more participants.
     * @param app The appointment object to check.
     * @return true if current participants are less than max allowed; false otherwise.
     */
    public boolean hasCapacity(Appointment app) {
        return app.getCurrentParticipants() < app.getMaxParticipants();
    }

    /**
     * US4.1 - Cancels a booked appointment if its start time is in the future.
     * Triggers a notification upon successful cancellation.
     * @param appointmentId The ID of the appointment to cancel.
     * @return true if the cancellation was successful; false otherwise.
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
     * US4.2 - Allows an administrator to cancel any appointment regardless of the time.
     * @param appointmentId The ID of the appointment to cancel.
     * @param user The user object requesting the cancellation.
     * @return true if the user is an admin and the appointment was found; false otherwise.
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

    /**
     * Filters and retrieves a list of appointments based on their type.
     * If type is empty, returns all appointments.
     * @param type The string representation of the appointment type (e.g., "Urgent").
     * @return A list of appointments matching the specified type.
     */
    public List<Appointment> getAppointmentsByType(String type) {
        if (type == null || type.isEmpty()) {
            return new ArrayList<>(appointments);
        }
        return appointments.stream()
                .filter(app -> app.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * US2.2 - Validates if the duration of an appointment is within the general limit.
     * Default limit is set to 120 minutes.
     * @param app The appointment object to check.
     * @return true if duration is valid (1-120 minutes); false otherwise.
     */
    public boolean isValidDuration(Appointment app) {
        long duration = java.time.Duration.between(app.getStartTime(), app.getEndTime()).toMinutes();
        return duration > 0 && duration <= 120;
    }

    /**
     * Returns the list of registered notification observers.
     * @return List of NotificationService observers.
     */
    public List<NotificationService> getObservers() {
        return observers;
    }
}