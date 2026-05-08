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
    
    
    @Test
    void testAdvancedMenuOptions() {
       
        String input = "\n\n3\n1\n4\nGeneral\n0\nshutdown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }

    @Test
    void testUserCancellationFlow() {
        
        String input = "Maha\n123\n3\n1\n0\nshutdown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }
    
    @Test
    void testEmptyInputsAndNavigation() {
        String input = "\n\n\n4\n\n0\nshutdown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }
    
    @Test
    void testAdminAddAppointmentError() {
        String input = "\n\n2\ninvalid\n0\nshutdown\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        MainMenu menu = new MainMenu();
        assertDoesNotThrow(() -> menu.start());
    }
}