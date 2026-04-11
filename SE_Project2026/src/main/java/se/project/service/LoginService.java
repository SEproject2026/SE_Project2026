package se.project.service;

import se.project.domain.User;
import java.util.ArrayList;
import java.util.List;

public class LoginService {
    private List<User> users;
    private boolean isLoggedIn = false; 
    public LoginService() {
        this.users = new ArrayList<>();
        users.add(new User("admin", "admin123", true));
    }

    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.isAdmin()) {
                isLoggedIn = true; 
                return true;
            }
        }
        return false;
    }

    
    public void logout() {
        isLoggedIn = false;
    }

    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}