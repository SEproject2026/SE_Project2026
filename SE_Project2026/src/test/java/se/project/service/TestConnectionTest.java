package se.project.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.project.presentation.TestConnection;


public class TestConnectionTest {
	@Test
    void testConnectionExecution() {
        assertDoesNotThrow(() -> {
            TestConnection.main(new String[]{});
        }, "The connection main method should execute without throwing exceptions");
    }
}
