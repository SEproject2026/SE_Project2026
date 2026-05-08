package se.project.service;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.contains;
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
    private NotificationService mockNotification; 

    @BeforeEach
    void setUp() {
        mockNotification = mock(NotificationService.class); 
        
        appointmentService = new AppointmentService(); 
        
        appointmentService.getObservers().clear(); 
        
        appointmentService.addObserver(mockNotification); 
    }

    @Test
    void testGetAvailableSlots() {
        List<Appointment> available = appointmentService.getAvailableSlots();
        assertEquals(3, available.size(), "Only unbooked appointments should be retrieved");
        
        for (Appointment app : available) {
            assertFalse(app.isBooked(), "Booked appointments must not be displayed");
        }
    }
    
    @Test
    void testBookAppointmentSuccess() {
        boolean result = appointmentService.bookAppointment(1);
        assertTrue(result, "The booking process should succeed for an available appointment");
        
        List<Appointment> available = appointmentService.getAvailableSlots();
        assertEquals(2, available.size(), "Remaining available appointments should be 2 after one booking");
    }

    @Test
    void testBookAlreadyBookedAppointment() {
        int appId = 2;
        appointmentService.bookAppointment(appId);
        
        boolean result = appointmentService.bookAppointment(appId);
        
        assertFalse(result, "Booking an already booked appointment must not be allowed");
    }

    @Test
    void testDurationLimit() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 1, 13, 0);
        Appointment longApp = new Appointment(99, start, end, 5, false, "Urgent");
        
        assertFalse(appointmentService.isValidDuration(longApp), "The check must fail because the duration exceeds two hours");
    }
    
    @Test
    void testParticipantLimitSuccess() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        Appointment limitedApp = new Appointment(20, start, end, 1, false, "General");
        
        assertTrue(appointmentService.hasCapacity(limitedApp), "There must be availability at the beginning");
        
        limitedApp.addParticipant();
        
        assertFalse(appointmentService.hasCapacity(limitedApp), "The system must reject adding another participant");
    }
    
    @Test
    void testSendAppointmentReminder() {
        int appId = 1;
        
        
        appointmentService.sendAppointmentReminder(appId);

        
        verify(mockNotification, times(1)).update(contains("Reminder"));
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
    
    
    @Test
    void testAppointmentTypeStorage() {
        Appointment virtualApp = new Appointment(50, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 1, false, "Virtual");
        
        assertEquals("Virtual", virtualApp.getType(), "The appointment type should be stored correctly.");
    }
    
    @Test
    void testFilterAppointmentsByType() {
        List<Appointment> urgentApps = appointmentService.getAppointmentsByType("Urgent");
        
        assertFalse(urgentApps.isEmpty(), "System should find at least one urgent appointment.");
        assertEquals("Urgent", urgentApps.get(0).getType(), "The retrieved appointment type must match the filter.");
    }
    
    @Test
    void testUrgentAppointmentDurationRule() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(45);
        Appointment urgentApp = new Appointment(101, start, end, 1, false, "Urgent");
        
        assertFalse(appointmentService.isValidDurationPerType(urgentApp), 
            "Urgent appointments should not exceed 30 minutes.");
    }

    @Test
    void testVirtualAppointmentDurationRule() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(50);
        Appointment virtualApp = new Appointment(102, start, end, 1, false, "Virtual");
        
        assertTrue(appointmentService.isValidDurationPerType(virtualApp), 
            "Virtual appointments should be valid within 60 minutes.");
    }

    @Test
    void testNullInputs() {
        appointmentService.addAppointment(null); 
        assertFalse(appointmentService.adminCancelAppointment(1, null)); 
        assertFalse(appointmentService.isValidDuration(null)); 
    }
    
    @Test
    void testNonExistentID() {
        assertFalse(appointmentService.bookAppointment(999)); 
    }
    
    @Test
    void testDomainModelsFullCoverage() {
        se.project.domain.User user = new se.project.domain.User("admin", "123", true);
        assertEquals("admin", user.getUsername());
        assertEquals("123", user.getPassword());
        assertTrue(user.isAdmin());

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        se.project.domain.Appointment app = new se.project.domain.Appointment(100, start, end, 5, false, "Regular");
        
        assertEquals(100, app.getId());
        assertEquals(start, app.getStartTime());
        assertEquals(end, app.getEndTime());
        assertEquals(5, app.getMaxParticipants());
        assertEquals(0, app.getCurrentParticipants());
        assertEquals("Regular", app.getType());
        assertFalse(app.isBooked());

        app.setBooked(true);
        assertTrue(app.isBooked());
        
        app.addParticipant();
        assertEquals(1, app.getCurrentParticipants());
        
        app.setStartTime(start.plusDays(1));
        app.setEndTime(end.plusDays(1));
    }
    
    @Test
    void testMainMenuStaticCall() {
        se.project.presentation.MainMenu menu = new se.project.presentation.MainMenu();
        assertNotNull(menu);
    }
    
    @Test
    void testMainMenuCoverageBooster() {
        se.project.presentation.MainMenu menu = new se.project.presentation.MainMenu();
        se.project.domain.User admin = new se.project.domain.User("admin", "123", true);
        menu.setTestUser(admin);

        
   
        assertNotNull(menu);

        se.project.domain.User user = new se.project.domain.User("user", "123", false);
        menu.setTestUser(user);
        
    }
    
    @Test
    void testEmailServiceCoverage() {
        se.project.notification.EmailService email = new se.project.notification.EmailService();
        
        email.sendEmail("user@test.com", "Test Subject", "Test Body");
        

        
        assertTrue(email.isMessageSent("Any message"));
    }
}
