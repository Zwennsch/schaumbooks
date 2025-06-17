package de.schaumburg.schaumbooks.data_handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.schaumburg.schaumbooks.book.Book;
import de.schaumburg.schaumbooks.book.BookRepository;
import de.schaumburg.schaumbooks.book.BookStatus;
import de.schaumburg.schaumbooks.user.UserRepository;

@Component
public class CsvBookLoader {

    private static final Logger logger = LoggerFactory.getLogger(CsvBookLoader.class);

    private BookRepository bookRepository;

    private UserRepository userRepository;

    public CsvBookLoader(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void readBookDataFromCsvAndSave(String csvFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip first line:
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    logger.warn("Skipping invalid line: {}", line);
                    continue;
                }
                String title = parts[0].trim();
                String verlag = parts[1].trim();
                String isbn = parts[2].trim();

                int numOfBooks;
                try {
                    numOfBooks = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    logger.warn("Invalid number format for number of books on line: {}", line);
                    continue;
                }

                for (int i = 0; i < numOfBooks; i++) {
                    Book book = new Book(null, title, verlag, isbn, BookStatus.AVAILABLE, null);
                    this.bookRepository.save(book);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading CSV file: {}", e.getMessage(), e);
            // throw e; // Rethrow the exception to handle it elsewhere if necessary
        }
    }

    

}
