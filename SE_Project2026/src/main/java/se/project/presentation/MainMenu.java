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
        boolean keepRunningSystem = true;

        while (keepRunningSystem) { // Loop خارجية لضمان البقاء في نظام تسجيل الدخول
            System.out.println("\n=== SYSTEM GATEWAY ===");
            System.out.println("--- Login System (Type 'shutdown' to close program) ---");
            System.out.flush();

            String username = "admin";
            String password = "123";

            try {
                System.out.print("Enter Username: ");
                System.out.flush();
                if (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.equalsIgnoreCase("shutdown")) {
                        keepRunningSystem = false;
                        break; 
                    }
                    if (!line.isEmpty()) username = line;
                }

                System.out.print("Enter Password: ");
                System.out.flush();
                if (scanner.hasNextLine()) {
                    password = scanner.nextLine().trim();
                }
            } catch (Exception e) {
                username = "admin";
            }

            boolean isAdmin = username.equalsIgnoreCase("admin");
            this.currentUser = new User(username, password, isAdmin);
            System.out.println("\nLogin successful! Welcome, " + username);

            int choice = -1;
            while (choice != 0) { // Loop داخلية للمنيو الخاصة باليوزر الحالي
                displayMenu();
                System.out.print("Enter choice: ");
                System.out.flush();

                if (!scanner.hasNextLine()) {
                    keepRunningSystem = false; 
                    break;
                }

                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                try {
                    choice = Integer.parseInt(input);
                    if (choice == 0) {
                        System.out.println("Logging out " + username + "...");
                    } else {
                        handleChoice(choice);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    choice = -1;
                }
            }
            // بمجرد اختيار 0، تنتهي الـ loop الداخلية ونعود لبداية الـ keepRunningSystem
        }
        System.out.println("System Shutdown. Goodbye!");
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
        // تم حذف سطر "Enter choice" من هنا لأنه موجود في ميثود start
    }

    private void handleChoice(int choice) {
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

    // --- ميثودات مساعدة لتجنب أخطاء الـ Scanner داخل الميثودات الفرعية ---
    private String safeRead() {
        if (scanner.hasNextLine()) return scanner.nextLine().trim();
        return "";
    }

    private void showAllSlots() {
        List<Appointment> all = appointmentService.getAppointmentsByType(""); 
        System.out.println("\n--- All System Appointments ---");
        for (Appointment app : all) {
            System.out.println(app.getId() + " | " + app.getType() + " | Booked: " + app.isBooked());
        }
    }

    private void addAppointment() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(safeRead());
            System.out.print("Enter Type: ");
            String type = safeRead();
            
            Appointment newApp = new Appointment(id, LocalDateTime.now().plusDays(1), 
                                                LocalDateTime.now().plusDays(1).plusHours(1), 5, false, type);
            appointmentService.addAppointment(newApp); 
        } catch (Exception e) {
            System.out.println("Error adding appointment. Check your input.");
        }
    }

    private void adminCancel() {
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

    private void showAvailableSlots() {
        List<Appointment> slots = appointmentService.getAvailableSlots();
        System.out.println("\n--- Available Slots ---");
        if (slots.isEmpty()) System.out.println("No available slots.");
        for (Appointment app : slots) {
            System.out.println("ID: " + app.getId() + " | Type: " + app.getType());
        }
    }

    private void bookSlot() {
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

    private void cancelSlot() {
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

    private void filterByType() {
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
    
}