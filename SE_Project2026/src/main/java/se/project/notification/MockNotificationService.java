package se.project.notification;

import java.util.ArrayList;
import java.util.List;


public class MockNotificationService implements NotificationService {
    private List<String> sentMessages = new ArrayList<>();

    @Override
    public void update(String message) {
        sentMessages.add(message);
        System.out.println("[Mock Notification Received]: " + message);
    }

    @Override
    public boolean isMessageSent(String message) {
        return sentMessages.contains(message);
    }
}