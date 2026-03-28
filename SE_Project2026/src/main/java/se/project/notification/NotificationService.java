package se.project.notification;

/**
 * Observer Interface for the Notification System (US4.1 & Observer Pattern).
 * This allows the AppointmentService to notify different channels without knowing their details.
 */
public interface NotificationService {
    // الميثود الأساسية للنمط: يتم استدعاؤها عند حدوث تغيير في الموعد
    void update(String message);
    
    // ميثود مساعدة تستخدم في الـ JUnit Tests فقط
    boolean isMessageSent(String message);
}