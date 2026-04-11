package se.project.service.strategies;

import se.project.domain.Appointment;
import java.time.Duration;


public class DefaultRuleStrategy implements BookingRuleStrategy {
    @Override
    public boolean isValid(Appointment app) {
        long minutes = Duration.between(app.getStartTime(), app.getEndTime()).toMinutes();
        return minutes > 0 && minutes <= 120;
    }
}