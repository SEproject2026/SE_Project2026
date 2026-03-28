package se.project.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of NotificationService following the Observer Pattern.
 * It stores all sent messages in a list for verification during testing.
 */
public class MockNotificationService implements NotificationService {
    // نستخدم القائمة لتخزين كل الرسائل التي تم إرسالها خلال التست
    private List<String> sentMessages = new ArrayList<>();

    @Override
    public void update(String message) {
        // هذه هي الميثود التي يناديها الـ Service (Subject)
        sentMessages.add(message);
        System.out.println("[Mock Notification Received]: " + message);
    }

    @Override
    public boolean isMessageSent(String message) {
        // نتحقق إذا كانت الرسالة المطلوبة موجودة في القائمة
        return sentMessages.contains(message);
    }
}