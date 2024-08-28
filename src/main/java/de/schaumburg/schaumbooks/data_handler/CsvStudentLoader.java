package de.schaumburg.schaumbooks.data_handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.schaumburg.schaumbooks.student.Student;
import de.schaumburg.schaumbooks.student.StudentRepository;

@Component
public class CsvStudentLoader {

    private static final Logger logger = LoggerFactory.getLogger(CsvStudentLoader.class);
    private final StudentRepository studentRepository;

    public CsvStudentLoader(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void readStudentDataFromCsvAndSave(String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip header line
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    String firstName = parts[0].trim();
                    String lastName = parts[1].trim();
                    String className = parts[2].trim();
                    String email = parts[3].trim();
                    
                    // Create a new Student object and save it to the repository
                    Student student = new Student(null, firstName, lastName, className, email);
                    this.studentRepository.save(student);
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    logger.error("Skipping malformed line: {}", line, e);
                }
            }
        } catch (IOException e) {
            logger.error("An error occurred while reading the CSV file: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
        }
    }
}
