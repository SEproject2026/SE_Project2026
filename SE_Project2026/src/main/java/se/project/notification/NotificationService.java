package se.project.notification;

public interface NotificationService {
	void sendReminder(String message);
    boolean isMessageSent(String message);
}
