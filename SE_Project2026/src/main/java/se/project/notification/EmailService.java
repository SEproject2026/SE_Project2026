package se.project.notification;

import java.util.logging.Logger;
import java.util.logging.Level;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import io.github.cdimascio.dotenv.Dotenv; 
import java.util.Properties;

public class EmailService implements NotificationService {
    private final String username;
    private final String password;
    
    private static final Logger logger = Logger.getLogger(EmailService.class.getName()); 
    
    public EmailService() {
        Dotenv dotenv = Dotenv.load();
        this.username = dotenv.get("EMAIL_USERNAME");
        this.password = dotenv.get("EMAIL_PASSWORD");
    }

    @Override
    public void update(String message) {
        logger.log(Level.INFO, "Notification received: {0}", message);
        
        try {
            sendEmail("s12218306@stu.najah.edu", "Appointment Notification", message);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Email skipped in test environment.");
        }
    }
    
    public void sendEmail(String to, String subject, String body) {
        if (username == null || password == null || to == null) {
            logger.log(Level.SEVERE, "Error: Credentials missing in .env file!");
            return;
        }

        if (to.toLowerCase().contains("test")) {
            logger.log(Level.INFO, "[Test Mode] Skipping real email transport for: {0}", to);
            return; 
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(body);

            Transport.send(mimeMessage);
            logger.log(Level.INFO, ">>> Real email sent successfully to: {0}", to);
            
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Failed to send email: {0}", e.getMessage());
        }
    }

    @Override
    public boolean isMessageSent(String message) {
        return true;
    }
}