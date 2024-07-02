package de.schaumburg.schaumbooks.data_handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.stereotype.Component;

import de.schaumburg.schaumbooks.book.Book;
import de.schaumburg.schaumbooks.book.BookRepository;
import de.schaumburg.schaumbooks.book.BookStatus;

@Component
public class CsvBookLoader {

    private BookRepository bookRepository;

    public CsvBookLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void readBookDataFromCsvAndSave(String csvFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip first line:
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String title = parts[0].trim();
                String verlag = parts[1].trim();
                String isbn = parts[2].trim();
                int numOfBooks = Integer.parseInt(parts[3].trim());
                for (int i = 0; i < numOfBooks; i++) {
                    Book book = new Book(null, title, verlag, isbn, BookStatus.AVAILABLE, null);
                    this.bookRepository.save(book);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not find csv-file " + e.getMessage());
        }
    }

}
