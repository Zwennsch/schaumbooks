package de.schaumburg.schaumbooks.data_handler;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import de.schaumburg.schaumbooks.person.PersonRepository;

// FIXME: tests won't work because I am storing admin at the beginning.
public class CsvStudentLoaderTest {

    @Mock
    private PersonRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private CsvStudentLoader csvStudentLoader;

    private AutoCloseable closeable;
    private final String validCsvPath = "test_valid_students.csv";
    private final String oneInvalidEntryCsvPath = "test_one_Invalid_entry_students.csv";
    private final String onlyInvalidCsvPath = "test_invalid_students.csv";

    @BeforeEach
    void setUp() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // Create a valid CSV file for testing
        try (PrintWriter writer = new PrintWriter(new FileWriter(validCsvPath))) {
            writer.println("Klasse,Nachname,Vorname,LogIn,email,password");
            writer.println("10A,Doe,John,loginJohn,john.doe@example.com,12345");
            writer.println("10B,Smith,Jane,jane123,jane.smith@example.com,123456");
        }

        // Create an invalid CSV file for testing
        try (PrintWriter writer = new PrintWriter(new FileWriter(oneInvalidEntryCsvPath))) {
            writer.println("Klasse,Nachname,Vorname,LogIn,email,password");
            writer.println("10A,Doe,John,loginJohn,john.doe@example.com,123456");
            writer.println("10A,Doe,John,loginJohn1,john.doe1@example.com,123456");
            writer.println("10A,Doe,John,loginJohn2,john.doe2@example.com,123456");
            writer.println("10B,jane.smith@example.com");// Malformed line
        }

        // Create an invalid CSV with no valid students
        try (PrintWriter writer = new PrintWriter(new FileWriter(onlyInvalidCsvPath))) {
            writer.println("Klasse,Nachname,Vorname,LogIn,email,password");
            writer.println("10A,Doe,John,loginJohn,john.doeexample.com,123456"); // email wrong format
            writer.println("10B,jane.smith@example.com");// Malformed line
        }

    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        // Delete test files after each test
        new java.io.File(validCsvPath).delete();
        new java.io.File(oneInvalidEntryCsvPath).delete();
        new java.io.File(onlyInvalidCsvPath).delete();
    }

    @Test
    void testReadValidStudentDataFromCsvAndSave() throws IOException {
        // when(studentRepository.saveAll(anyList())).thenReturn(anyList());
        csvStudentLoader.readStudentDataFromCsvAndSave(validCsvPath);

        // Verify that saveAll() was called once with exactly 2 students
        verify(userRepository).saveAll(argThat(list -> list instanceof List<?> && ((List<?>) list).size() == 2));
    }

    @Test
    void testReadStudentDataFromNonExistentCsvFile() {
        // No exception should be thrown, but no students should be saved
        csvStudentLoader.readStudentDataFromCsvAndSave("non_existent_file.csv");

        // assertThrows(FileNotFoundException.class, () ->csvStudentLoader.readStudentDataFromCsvAndSave("non_existent_file_path"));

        // Verify that no students were saved to the repository
        verify(userRepository, times(0)).saveAll(anyList());
    }

    @Test
    void testReadStudentDataFromCsvWithMalformedLine() {
        // when(studentRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        csvStudentLoader.readStudentDataFromCsvAndSave(oneInvalidEntryCsvPath);

        // Verify that saveAll() was called once with exactly 3 students
        verify(userRepository).saveAll(argThat(list -> list instanceof List<?> && ((List<?>) list).size() == 3));
    }

    @Test
    void shouldCatchIllegalArgumentExceptionBecauseOfInvalidEmail() {
        csvStudentLoader.readStudentDataFromCsvAndSave(onlyInvalidCsvPath);

        verify(userRepository, times(0)).saveAll(anyList());
    }
}
