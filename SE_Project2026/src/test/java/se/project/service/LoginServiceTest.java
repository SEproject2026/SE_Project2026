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
        // إنشاء نسخة جديدة من الخدمة قبل كل تيسيت
        loginService = new LoginService();
    }

    @Test
    void testAdminLoginSuccess() {
        // فحص دخول ناجح ببيانات صحيحة
        boolean result = loginService.login("admin", "admin123");
        assertTrue(result, "البيانات صحيحة، يجب أن ينجح الدخول");
    }

    @Test
    void testAdminLoginFailure() {
        // فحص فشل الدخول بكلمة سر خاطئة
        boolean result = loginService.login("admin", "wrongPass");
        assertFalse(result, "كلمة السر خاطئة، يجب أن يفشل الدخول");
    }
    
    @Test
    void testAdminLogout() {
        // 1. تسجيل الدخول أولاً
        loginService.login("admin", "admin123");
        assertTrue(loginService.isLoggedIn(), "يجب أن يكون المسؤول مسجلاً للدخول الآن");

        // 2. تنفيذ تسجيل الخروج (US1.2)
        loginService.logout();

        // 3. التحقق من إغلاق الجلسة (Acceptance Criteria)
        assertFalse(loginService.isLoggedIn(), "بعد تسجيل الخروج، يجب أن تكون الحالة false");
    }
}
