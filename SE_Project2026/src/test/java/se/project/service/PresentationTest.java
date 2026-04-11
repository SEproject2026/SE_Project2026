package se.project.service;

import org.junit.jupiter.api.Test;
import se.project.presentation.MainMenu;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class PresentationTest {

    @Test
    void testMenuNavigation() {
        String input = "1\n0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        MainMenu menu = new MainMenu();
        
        assertDoesNotThrow(() -> menu.start());
    }

    @Test
    void testMenuBookingSimulation() {
        String input = "2\n1\n0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }
}