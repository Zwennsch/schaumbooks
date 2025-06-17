package de.schaumburg.schaumbooks.data_handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.schaumburg.schaumbooks.book.Book;
import de.schaumburg.schaumbooks.book.BookRepository;
import de.schaumburg.schaumbooks.book.BookStatus;
import de.schaumburg.schaumbooks.user.Role;
import de.schaumburg.schaumbooks.user.User;
import de.schaumburg.schaumbooks.user.UserRepository;

@Component
public class CsvStudentLoader {

    private static final Logger logger = LoggerFactory.getLogger(CsvStudentLoader.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;

    public CsvStudentLoader(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
            BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
    }

    public void readStudentDataFromCsvAndSave(String csvFilePath) {
        // TODO: Remove later admin added just for testing purposes.
        if (!userRepository.findByUsername("sven").isPresent()) { // Avoid duplicate admin entry
            userRepository.save(new User(null, "sven", passwordEncoder.encode("1234"),
                    "Sven", "Schröder",
                    "s.schroeder3@schule.bremen.de", List.of(Role.ADMIN, Role.TEACHER), null));
        }
        List<User> students = new ArrayList<>();
        int successCount = 0, failureCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip header line
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length < 6) {
                        throw new IllegalArgumentException("Incomplete Data");
                    }
                    String className = parts[0].trim();
                    String lastName = parts[1].trim();
                    String firstName = parts[2].trim();
                    String email = parts[4].trim();
                    String username = parts[3].trim();
                    String password = parts[5].trim();

                    // Validate Email Format:
                    if (!email.contains("@") || !email.contains(".")) {
                        throw new IllegalArgumentException("Invalid email format: " + email);
                    }

                    // Encrypt password before storing:
                    String encryptedPassword = passwordEncoder.encode(password);

                    // Create a new User and save it to the repository
                    User student = new User(null, username, encryptedPassword, firstName, lastName, email,
                            List.of(Role.STUDENT),
                            className);

                    students.add(student);
                    successCount++;
                    // this.userRepository.save(student);
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    logger.error("Skipping malformed line: {}", line, e);
                    failureCount++;
                }
            }
            if (!students.isEmpty()) {
                userRepository.saveAll(students);
                logger.info("Successsfully saved {} students to the database. ", successCount);
            }
            if (failureCount > 0) {
                logger.warn("Skipped {} malformated student records.", failureCount);
            }

        } catch (IOException e) {
            logger.error("An error occurred while reading the CSV file: {}", e.getMessage());
        }
        // TODO: Remove after testing
        try {
            add3BooksForStudent2();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO: Remove after testing
    @Transactional
    public void add3BooksForStudent2() throws InterruptedException {
        waitForUser(2l, 10, 1000);

        for (int i = 0; i < 3; i++) {
            Book b = new Book(null, "BookForStud2No " + i, "testVerlag", "123-3479-789", BookStatus.LENT,
                    userRepository.getReferenceById(2L));
            bookRepository.save(b);
        }

    }

    private void waitForUser(long userId, int maxRetries, long sleepMillis) throws InterruptedException {
        for (int i = 0; i < maxRetries; i++) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                System.out.println("user wit id 2 present!");
                return;
            }
            Thread.sleep(sleepMillis);
        }
        throw new RuntimeException("User with id: " + userId + " could not be found");
    }

}
