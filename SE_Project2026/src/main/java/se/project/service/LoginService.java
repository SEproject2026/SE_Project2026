package se.project.service;

import se.project.domain.User;
import java.util.ArrayList;
import java.util.List;

public class LoginService {
    private List<User> users;
    private boolean isLoggedIn = false; // تتبع حالة الجلسة

    public LoginService() {
        this.users = new ArrayList<>();
        users.add(new User("admin", "admin123", true));
    }

    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.isAdmin()) {
                isLoggedIn = true; // تفعيل الجلسة
                return true;
            }
        }
        return false;
    }

    /**
     * US1.2 - Administrator logout
     * Closes the session by setting isLoggedIn to false.
     */
    public void logout() {
        isLoggedIn = false; // إغلاق الجلسة
    }

    /**
     * Helper method to check if admin is still logged in.
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}