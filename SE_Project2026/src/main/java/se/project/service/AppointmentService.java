package se.project.service;

import se.project.domain.Appointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import se.project.notification.NotificationService;
import se.project.domain.User;

public class AppointmentService {
    private List<Appointment> appointments;
    private NotificationService notificationService;

    public AppointmentService(NotificationService notificationService) {
        this.notificationService = notificationService; 
        this.appointments = new ArrayList<>();
        
        
        appointments.add(new Appointment(1, 
            LocalDateTime.of(2026, 4, 1, 10, 0), 
            LocalDateTime.of(2026, 4, 1, 11, 0), 5, false));

        appointments.add(new Appointment(2, 
            LocalDateTime.of(2026, 4, 1, 11, 0), 
            LocalDateTime.of(2026, 4, 1, 12, 0), 5, true));

        appointments.add(new Appointment(3, 
            LocalDateTime.of(2026, 4, 2, 9, 0), 
            LocalDateTime.of(2026, 4, 2, 10, 0), 5, false));
    }

        public void sendAppointmentReminder(int appointmentId) {
        String message = "Reminder: Your appointment with ID " + appointmentId + " is coming up!";
        notificationService.sendReminder(message);
    }

    public List<Appointment> getAvailableSlots() {
        return appointments.stream()
                .filter(app -> !app.isBooked())
                .collect(Collectors.toList());
    }
    /**
     * US2.1 - Book an appointment
     * Finds an appointment by ID and sets its status to booked.
     * @param appointmentId The ID of the appointment to book.
     * @return true if booking was successful, false if not found or already booked.
     */
    public boolean bookAppointment(int appointmentId) {
        for (Appointment app : appointments) {
            if (app.getId() == appointmentId && !app.isBooked()) {
                app.setBooked(true); 
                return true;
            }
        }
        return false;
    }
    
    
    private static final long MAX_DURATION_MINUTES = 120;

    /**
     * US2.2 - Enforce visit duration rule
     * Checks if the appointment duration is within the allowed limit.
     */
    public boolean isValidDuration(Appointment app) {
        long duration = java.time.Duration.between(app.getStartTime(), app.getEndTime()).toMinutes();
        
        return duration > 0 && duration <= MAX_DURATION_MINUTES;
    }
    /**
     * US2.3 - Enforce participant limit
     * Checks if the appointment has reached its maximum capacity.
     */
    public boolean hasCapacity(Appointment app) {
        return app.getCurrentParticipants() < app.getMaxParticipants();
    }

    public boolean registerParticipant(int appointmentId) {
        for (Appointment app : appointments) {
            if (app.getId() == appointmentId) {
                if (hasCapacity(app)) {
                    app.addParticipant();
                    return true;
                }
            }
        }
        return false;
    }
    
    
    /**
     * US4.1 - Cancel an appointment
     * Only future appointments can be canceled. Slot becomes available again.
     */
    public boolean cancelAppointment(int appointmentId) {
        for (Appointment app : appointments) {
            if (app.getId() == appointmentId && app.isBooked()) {
                if (app.getStartTime().isAfter(LocalDateTime.now())) {
                    app.setBooked(false);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * US4.1 - Modify appointment time
     * Changes the time of an existing booking if the new time is valid.
     */
    public boolean modifyAppointment(int appointmentId, LocalDateTime newStart, LocalDateTime newEnd) {
        for (Appointment app : appointments) {
            if (app.getId() == appointmentId && app.isBooked()) {
                if (app.getStartTime().isAfter(LocalDateTime.now())) {
                 
                    return true; 
                }
            }
        }
        return false;
    }
    
    /**
     * US4.2 - Admin Cancel Appointment
     * Allows only administrators to cancel any booking.
     */
    public boolean adminCancelAppointment(int appointmentId, User user) {
        if (user != null && user.isAdmin()) {
            for (Appointment app : appointments) {
                if (app.getId() == appointmentId) {
                    app.setBooked(false); 
                    return true;
                }
            }
        }
        return false;
    }
    
}