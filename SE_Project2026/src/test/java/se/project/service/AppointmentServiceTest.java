package se.project.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.project.domain.Appointment;
import se.project.notification.MockNotificationService;
import se.project.notification.NotificationService;

import java.util.List;
import java.time.LocalDateTime;

class AppointmentServiceTest {

    private AppointmentService appointmentService;
    private MockNotificationService mockNotification; 

    @BeforeEach
    void setUp() {
        // 1. إنشاء النسخة الوهمية من خدمة التنبيهات
        mockNotification = new MockNotificationService();
        
        // 2. تمرير الـ Mock للخدمة (Dependency Injection)
        // إذا استمر الخطأ هنا، تأكدي أنكِ حفظتِ ملف AppointmentService.java بعد تعديل الـ Constructor
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
        // تأكدي أن كلاس Appointment يدعم (id, start, end, maxParticipants, isBooked)
        Appointment longApp = new Appointment(99, start, end, 5, false);
        
        assertFalse(appointmentService.isValidDuration(longApp), "The check must fail because the duration exceeds two hours");
    }
    
    @Test
    void testParticipantLimitSuccess() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        // إنشاء موعد بسعة 1 فقط للاختبار
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
        assertTrue(mockNotification.isMessageSent(expectedMessage), "يجب أن يسجل الـ Mock إرسال التذكير بنجاح");
    }
}