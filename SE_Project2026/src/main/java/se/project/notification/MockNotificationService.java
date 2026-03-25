package se.project.notification;

import java.util.ArrayList;
import java.util.List;

public class MockNotificationService implements NotificationService {
    private List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendReminder(String message) {
        sentMessages.add(message);
        System.out.println("Mock Notification Sent: " + message);
    }

    @Override
    public boolean isMessageSent(String message) {
        return sentMessages.contains(message);
    }
}