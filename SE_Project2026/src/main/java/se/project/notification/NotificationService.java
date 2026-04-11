package se.project.notification;

public interface NotificationService {
    void update(String message);
    
    boolean isMessageSent(String message);
}