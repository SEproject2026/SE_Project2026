package se.project.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.project.domain.Appointment;
import se.project.notification.MockNotificationService;
import se.project.notification.NotificationService;
import se.project.domain.User;

import java.util.List;
import java.time.LocalDateTime;

class AppointmentServiceTest {

    private AppointmentService appointmentService;
    private MockNotificationService mockNotification; 

    @BeforeEach
    void setUp() {
        mockNotification = new MockNotificationService();
        
        appointmentService = new AppointmentService(mockNotification);
    }

    @Test
    void testGetAvailableSlots() {
        List<Appointment> available = appointmentService.getAvailableSlots();
        assertEquals(2, available.size(), "Only unbooked appointments should be retrieved");
        
        for (Appointment app : available) {
            assertFalse(app.isBooked(), "Booked appointments must not be displayed");
        }
    }
    
    @Test
    void testBookAppointmentSuccess() {
        boolean result = appointmentService.bookAppointment(1);
        assertTrue(result, "The booking process should succeed for an available appointment");
        
        List<Appointment> available = appointmentService.getAvailableSlots();
        assertEquals(1, available.size(), "Only one appointment (number 3) should remain available");
    }

    @Test
    void testBookAlreadyBookedAppointment() {
        boolean result = appointmentService.bookAppointment(2);
        assertFalse(result, "Booking an already booked appointment must not be allowed");
    }

    @Test
    void testDurationLimit() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 1, 13, 0);
        Appointment longApp = new Appointment(99, start, end, 5, false);
        
        assertFalse(appointmentService.isValidDuration(longApp), "The check must fail because the duration exceeds two hours");
    }
    
    @Test
    void testParticipantLimitSuccess() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        Appointment limitedApp = new Appointment(20, start, end, 1, false);
        
        assertTrue(appointmentService.hasCapacity(limitedApp), "There must be availability at the beginning");
        
        limitedApp.addParticipant();
        
        assertFalse(appointmentService.hasCapacity(limitedApp), "The system must reject adding another participant");
    }
    
    @Test
    void testSendAppointmentReminder() {
        int appId = 1;
        appointmentService.sendAppointmentReminder(appId);
        
        String expectedMessage = "Reminder: Your appointment with ID 1 is coming up!";
        assertTrue(mockNotification.isMessageSent(expectedMessage), "The mock should record that the reminder was sent successfully");
    }
    
    @Test
    void testCancelAppointment() {
        appointmentService.bookAppointment(1);
        
        boolean result = appointmentService.cancelAppointment(1);
        
        assertTrue(result, "Cancellation should succeed for a booked future appointment");        
        List<Appointment> available = appointmentService.getAvailableSlots();
        boolean isFound = available.stream().anyMatch(a -> a.getId() == 1);
        assertTrue(isFound, "The appointment should be available again after cancellation");    }
    
    @Test
    void testAdminCancelSuccess() {
        User admin = new User("admin", "123", true);
        
        appointmentService.bookAppointment(1);
        
        boolean result = appointmentService.adminCancelAppointment(1, admin);
        
        assertTrue(result,"Administrator must be able to cancel the booking");
    }

    @Test
    void testUserCannotAdminCancel() {
        User regularUser = new User("user", "123", false);
        
        appointmentService.bookAppointment(1);
        
        boolean result = appointmentService.adminCancelAppointment(1, regularUser);
        
        assertFalse(result, "A regular user should not have administrator cancellation privileges");   
        }
}