package se.project.service.strategies;

import se.project.domain.Appointment;
import java.time.Duration;

/**
 * Strategy for standard appointments (e.g., Follow-up, General).
 * Rule: Duration must be between 1 and 120 minutes.
 * @author YourName
 */
public class DefaultRuleStrategy implements BookingRuleStrategy {
    @Override
    public boolean isValid(Appointment app) {
        long minutes = Duration.between(app.getStartTime(), app.getEndTime()).toMinutes();
        // الحد الأقصى للمواعيد العادية هو 120 دقيقة (ساعتان)
        return minutes > 0 && minutes <= 120;
    }
}