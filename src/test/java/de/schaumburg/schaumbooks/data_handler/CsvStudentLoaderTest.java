package de.schaumburg.schaumbooks.data_handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.schaumburg.schaumbooks.user.User;
import de.schaumburg.schaumbooks.user.UserRepository;

// FIXME: tests won't work because I am storing admin at the beginning.
public class CsvStudentLoaderTest {

    @Mock
    private UserRepository studentRepository;

    @InjectMocks
    private CsvStudentLoader csvStudentLoader;

    private AutoCloseable closeable;
    private final String validCsvPath = "test_valid_students.csv";
    private final String invalidCsvPath = "test_invalid_students.csv";

    @BeforeEach
    void setUp() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);
        
        // Create a valid CSV file for testing
        try (PrintWriter writer = new PrintWriter(new FileWriter(validCsvPath))) {
            writer.println("Klasse,Nachname,Vorname,LogIn,email,password");
            writer.println("10A,Doe,John,loginJohn,john.doe@example.com,12345");
            writer.println("10B,Smith,Jane,jane123,jane.smith@example.com,123456");
        }
        
        // Create an invalid CSV file for testing
        try (PrintWriter writer = new PrintWriter(new FileWriter(invalidCsvPath))) {
            writer.println("Klasse,Nachname,Vorname,LogIn,email,password");
            writer.println("10A,Doe,John,loginJohn,john.doe@example.com,123456");
            writer.println("10B,jane.smith@example.com");// Malformed line
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        // Delete test files after each test
        new java.io.File(validCsvPath).delete();
        new java.io.File(invalidCsvPath).delete();
    }

    @Test
    void testReadValidStudentDataFromCsvAndSave() throws IOException {
        csvStudentLoader.readStudentDataFromCsvAndSave(validCsvPath);

        // Verify that two students were saved to the repository
        verify(studentRepository, times(2)).save(any(User.class));
    }

    @Test
    void testReadStudentDataFromNonExistentCsvFile() {
        // No exception should be thrown, but no students should be saved
        csvStudentLoader.readStudentDataFromCsvAndSave("non_existent_file.csv");

        // Verify that no students were saved to the repository
        verify(studentRepository, times(0)).save(any(User.class));
    }

    @Test
    void testReadStudentDataFromCsvWithMalformedLine() {
        csvStudentLoader.readStudentDataFromCsvAndSave(invalidCsvPath);

        // Verify that only the first student was saved to the repository
        verify(studentRepository, times(1)).save(any(User.class));
    }
}
