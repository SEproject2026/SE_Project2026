package se.project.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Administrator Login (US1.1)
 */
class LoginServiceTest {

    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService();
    }

    @Test
    void testAdminLoginSuccess() {
        boolean result = loginService.login("admin", "admin123");
        assertTrue(result, "The data is correct; the login should succeed");
    }

    @Test
    void testAdminLoginFailure() {
        boolean result = loginService.login("admin", "wrongPass");
        assertFalse(result, "The password is incorrect; the login should fail");
    }
    
    @Test
    void testAdminLogout() {
        loginService.login("admin", "admin123");
        assertTrue(loginService.isLoggedIn(), "The admin must be logged in now");

        loginService.logout();

        assertFalse(loginService.isLoggedIn(), "After logging out, the status must be false");
    }
}
