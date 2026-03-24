package se.project.domain;

/**
 * Represents a user in the Appointment Scheduling System.
 * @author YourName
 * @version 1.0
 */
public class User {
    private String username;
    private String password;
    private boolean isAdmin;

    /**
     * Constructor for User.
     * @param username The login username.
     * @param password The login password.
     * @param isAdmin True if the user is an administrator.
     */
    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return isAdmin; }
}