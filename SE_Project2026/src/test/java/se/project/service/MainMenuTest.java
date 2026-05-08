package se.project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import se.project.presentation.MainMenu;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class MainMenuTest {

    @Test
    void testFullAdminFlowSilent() {
        String input = "\n\n2\n500\nGeneral\n0\nshutdown\n";
        provideInput(input);

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }

    @Test
    void testUserBookingFlowSilent() {
        String input = "Maha\n123\n2\n1\n0\nshutdown\n";
        provideInput(input);

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }

    @Test
    void testInvalidOptionsSilent() {
        String input = "\n\nabc\n99\n0\nshutdown\n";
        provideInput(input);

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }
}