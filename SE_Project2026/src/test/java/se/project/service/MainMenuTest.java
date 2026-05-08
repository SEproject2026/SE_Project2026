package se.project.service;

import org.junit.jupiter.api.Test;
import se.project.presentation.MainMenu;
import se.project.domain.User;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class MainMenuTest {

    @Test
    void testSystemShutdown() {
        String input = "shutdown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        assertDoesNotThrow(() -> {
            MainMenu.main(new String[]{});
        });
    }

    @Test
    void testAdminNavigationAndLogout() {
        String input = "\n\n0\nshutdown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }

    @Test
    void testInvalidInputHandling() {
       
        String input = "\n\nabc\n99\n0\nshutdown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }

    @Test
    void testUserRoleFlow() {
     
        String input = "Maha\npassword\n1\n0\nshutdown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }
}