package se.project.service.strategies;
import se.project.domain.Appointment;

public interface BookingRuleStrategy {
    boolean isValid(Appointment appointment);
}