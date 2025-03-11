package de.schaumburg.schaumbooks.data_handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.schaumburg.schaumbooks.user.Role;
import de.schaumburg.schaumbooks.user.User;
import de.schaumburg.schaumbooks.user.UserRepository;

@Component
public class CsvStudentLoader {

    private static final Logger logger = LoggerFactory.getLogger(CsvStudentLoader.class);
    private final UserRepository userRepository;

    public CsvStudentLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void readStudentDataFromCsvAndSave(String csvFilePath) {
        // TODO: Remove later admin added jsut for testing purposes.
        userRepository.save(new User(null, "sven", "1234", "Sven", "Schröder", "s.schroeder3@schule.bremen.de", List.of(Role.ADMIN, Role.TEACHER), null));
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip header line
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    String className = parts[0].trim();
                    String lastName = parts[1].trim();
                    String firstName = parts[2].trim();
                    String email = parts[4].trim();
                    String username = parts[3].trim();
                    String password = parts[5].trim();

                    // TODO: before storing into the database the password it should be encrypted
                    // Create a new User and save it to the repository
                    User student = new User(null, username, password, firstName, lastName, email, List.of(Role.STUDENT),
                            className);
                    this.userRepository.save(student);
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    logger.error("Skipping malformed line: {}", line, e);
                }
            }
        } catch (IOException e) {
            logger.error("An error occurred while reading the CSV file: {}", e.getMessage());
        }
    }
}
