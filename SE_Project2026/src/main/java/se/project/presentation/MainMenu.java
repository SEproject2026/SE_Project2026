package se.project.presentation;

import se.project.domain.Appointment;
import se.project.domain.User;
import se.project.service.AppointmentService;
import se.project.notification.MockNotificationService;
import se.project.notification.EmailService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private AppointmentService appointmentService;
    private Scanner scanner;
    private User currentUser;
    private static final String ADMIN_UID = "admin";
    private static final String ADMIN_KEY = "123";
    
    public MainMenu() {
        this.appointmentService = new AppointmentService();
        this.scanner = new Scanner(System.in);
        
        
        this.appointmentService.addObserver(new MockNotificationService());
        try {
            this.appointmentService.addObserver(new EmailService());
        } catch (Exception e) {
        	
            System.out.println("Warning: Email service could not start.");
        }
    }

    
    public void start() {
        while (true) {
            String loginUser = performLogin();
            
            if (loginUser == null) break;
            runMenuLoop(loginUser);
        }
        System.out.println("System Shutdown. Goodbye!");
    }
 
    private void processChoice(String input, String username) {
        try {
            int selected = Integer.parseInt(input);
            if (selected == 0) {
                System.out.println("Logging out " + username + "...");
            } else {
                handleChoice(selected);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private void runMenuLoop(String user) {
        int choice = -1;
        while (choice != 0) {
            displayMenu();
            System.out.print("Enter choice: ");
            if (!scanner.hasNextLine()) break;
            
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            
            processChoice(input, user);
            if (input.equals("0")) choice = 0;
        }
    }
    private String performLogin() {
        String user = ADMIN_UID;
        String pass = ADMIN_KEY;
        try {
            System.out.print("Enter Username: ");
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("shutdown")) return null;
                if (!line.isEmpty()) user = line;
            }
            System.out.print("Enter Password: ");
            if (scanner.hasNextLine()) pass = scanner.nextLine().trim();
        } catch (Exception e) {
            user = ADMIN_UID;
        }
        this.currentUser = new User(user, pass, user.equalsIgnoreCase(ADMIN_UID));
        System.out.println("\nLogin successful! Welcome, " + user);
        return user;
    }
    
    private void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        if (currentUser.isAdmin()) {
            System.out.println("1. View All Appointments (Admin)");
            System.out.println("2. Add New Appointment Slot (Admin)");
            System.out.println("3. Force Cancel Any Appointment (Admin)");
        } else {
            System.out.println("1. View Available Slots (User)");
            System.out.println("2. Book an Appointment (User)");
            System.out.println("3. Cancel My Appointment (User)");
        }
        System.out.println("4. Filter Appointments by Type");
        System.out.println("0. Exit");
    }

    public void handleChoice(int choice) {
        if (currentUser.isAdmin()) {
            switch (choice) {
                case 1: showAllSlots(); break;
                case 2: addAppointment(); break;
                case 3: adminCancel(); break;
                case 4: filterByType(); break;
                default: System.out.println("Invalid choice.");
            }
        } else {
            switch (choice) {
                case 1: showAvailableSlots(); break;
                case 2: bookSlot(); break;
                case 3: cancelSlot(); break;
                case 4: filterByType(); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    protected String safeRead() {
        if (scanner.hasNextLine()) return scanner.nextLine().trim();
        return "";
    }

    protected void showAllSlots() {
        List<Appointment> all = appointmentService.getAppointmentsByType(""); 
        System.out.println("\n--- All System Appointments ---");
        for (Appointment app : all) {
            System.out.println(app.getId() + " | " + app.getType() + " | Booked: " + app.isBooked());
        }
    }

    protected void addAppointment() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(safeRead());
            System.out.print("Enter Type: ");
            String type = safeRead();
            
            Appointment newApp = new Appointment(id, LocalDateTime.now().plusDays(1), 
            LocalDateTime.now().plusDays(1).plusHours(1), 5, false, type);
            appointmentService.addAppointment(newApp); 
            appointmentService.sendAppointmentReminder(id);
        } catch (Exception e) {
            System.out.println("Error adding appointment. Check your input.");
        }
    }

    protected void adminCancel() {
        try {
            System.out.print("Enter ID to Force Cancel: ");
            int id = Integer.parseInt(safeRead());
            if (appointmentService.adminCancelAppointment(id, currentUser)) {
                System.out.println("Admin Force Cancelled ID: " + id);
            } else {
                System.out.println("Failed to cancel.");
            }
        } catch (Exception e) {
            System.out.println("Invalid ID.");
        }
    }

    protected void showAvailableSlots() {
        List<Appointment> slots = appointmentService.getAvailableSlots();
        System.out.println("\n--- Available Slots ---");
        if (slots.isEmpty()) System.out.println("No available slots.");
        for (Appointment app : slots) {
            System.out.println("ID: " + app.getId() + " | Type: " + app.getType());
        }
    }

    protected void bookSlot() {
        try {
            System.out.print("Enter ID to book: ");
            int id = Integer.parseInt(safeRead());
            if (appointmentService.bookAppointment(id)) {
                System.out.println("Successfully booked!");
            } else {
                System.out.println("Booking failed (Already booked or ID not found).");
            }
        } catch (Exception e) {
            System.out.println("Invalid ID.");
        }
    }

    protected void cancelSlot() {
        try {
            System.out.print("Enter ID to cancel: ");
            int id = Integer.parseInt(safeRead());
            if (appointmentService.cancelAppointment(id)) {
                System.out.println("Successfully canceled.");
            } else {
                System.out.println("Failed to cancel.");
            }
        } catch (Exception e) {
            System.out.println("Invalid ID.");
        }
    }

    protected void filterByType() {
        System.out.print("Enter Type: ");
        String type = safeRead();
        List<Appointment> filtered = appointmentService.getAppointmentsByType(type);
        if (filtered.isEmpty()) System.out.println("No appointments found for this type.");
        for (Appointment app : filtered) {
            System.out.println("ID: " + app.getId() + " | Type: " + app.getType() + " | Booked: " + app.isBooked());
        }
    }

    public static void main(String[] args) {
        new MainMenu().start();
    }
    
    public void setTestUser(User user) {
        this.currentUser = user;
    }
    
}