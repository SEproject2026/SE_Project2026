package se.project.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import se.project.presentation.TestConnection;


public class TestConnectionTest {
	void testConnectionExecution() {
	    assertDoesNotThrow(() -> TestConnection.main(new String[]{}));
	}
}
