package se.project.service;

import se.project.domain.Appointment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentService {
    private List<Appointment> appointments;

    public AppointmentService() {
        this.appointments = new ArrayList<>();
        // إضافة مواعيد باستخدام LocalDateTime.of(year, month, day, hour, minute)
        appointments.add(new Appointment(1, LocalDateTime.of(2026, 4, 1, 10, 0), false));
        appointments.add(new Appointment(2, LocalDateTime.of(2026, 4, 1, 11, 0), true));
        appointments.add(new Appointment(3, LocalDateTime.of(2026, 4, 2, 9, 0), false));
    }

    public List<Appointment> getAvailableSlots() {
        return appointments.stream()
                .filter(app -> !app.isBooked())
                .collect(Collectors.toList());
    }
}