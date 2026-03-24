package se.project.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.project.domain.Appointment;
import java.util.List;

class AppointmentServiceTest {
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService();
    }

    @Test
    void testGetAvailableSlots() {
        // تنفيذ العملية
        List<Appointment> available = appointmentService.getAvailableSlots();

        // التأكد أن العدد 2 بناءً على البيانات التي وضعناها في الخدمة
        assertEquals(2, available.size(), "يجب استرجاع المواعيد غير المحجوزة فقط");
        
        // التأكد من أن كل موعد مسترجع ليس محجوزاً
        for (Appointment app : available) {
            assertFalse(app.isBooked(), "لا يجوز عرض موعد محجوز");
        }
    }
}