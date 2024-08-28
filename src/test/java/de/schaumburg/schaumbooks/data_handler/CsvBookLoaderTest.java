package de.schaumburg.schaumbooks.data_handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// import java.io.BufferedReader;
// import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.schaumburg.schaumbooks.book.Book;
import de.schaumburg.schaumbooks.book.BookRepository;
import de.schaumburg.schaumbooks.book.BookStatus;

class CsvBookLoaderTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CsvBookLoader csvBookLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReadBooksFromCsvAndSaveToRepository() throws IOException {
        // Create a temporary CSV file
        Path tempCsvFile = Files.createTempFile("books", ".csv");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tempCsvFile))) {
            writer.println("title,verlag,isbn,numOfBooks");
            writer.println("Book One,Publisher One,1234567890,1");
            writer.println("Book Two,Publisher Two,0987654321,2");
        }

        // Run the method under test
        csvBookLoader.readBookDataFromCsvAndSave(tempCsvFile.toString());

        // Capture the Book objects saved to the repository
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(3)).save(bookCaptor.capture());

        // Verify the content of saved books
        assertEquals(3, bookCaptor.getAllValues().size());
        assertEquals("Book One", bookCaptor.getAllValues().get(0).getTitle());
        assertEquals("Publisher One", bookCaptor.getAllValues().get(0).getVerlag());
        assertEquals("1234567890", bookCaptor.getAllValues().get(0).getIsbn());
        assertEquals(BookStatus.AVAILABLE, bookCaptor.getAllValues().get(0).getStatus());

        assertEquals("Book Two", bookCaptor.getAllValues().get(1).getTitle());
        assertEquals("Publisher Two", bookCaptor.getAllValues().get(1).getVerlag());
        assertEquals("0987654321", bookCaptor.getAllValues().get(1).getIsbn());
        assertEquals(BookStatus.AVAILABLE, bookCaptor.getAllValues().get(1).getStatus());
    }

    @Test
    void shouldHandleEmptyFileGracefully() throws IOException {
        // Create an empty temporary CSV file
        Path tempCsvFile = Files.createTempFile("empty_books", ".csv");

        // Run the method under test
        csvBookLoader.readBookDataFromCsvAndSave(tempCsvFile.toString());

        // Verify that no books were saved
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldHandleMalformedDataGracefully() throws IOException {
        // Create a temporary CSV file with malformed data
        Path tempCsvFile = Files.createTempFile("malformed_books", ".csv");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tempCsvFile))) {
            writer.println("title,verlag,isbn,numOfBooks");
            writer.println("Malformed Data Without Commas");
        }

        // Run the method under test
        csvBookLoader.readBookDataFromCsvAndSave(tempCsvFile.toString());

        // Verify that no books were saved due to the malformed data
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldHandleMalformedNumberOfBooksGracefully() throws IOException {
        // Create a temporary CSV file with malformed data
        Path tempCsvFile = Files.createTempFile("malformed_books", ".csv");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tempCsvFile))) {
            writer.println("title,verlag,isbn,numOfBooks");
            writer.println("Malformed, Data, Without, one");
        }

        // Run the method under test
        csvBookLoader.readBookDataFromCsvAndSave(tempCsvFile.toString());

        // Verify that no books were saved due to the malformed data
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldHandleFileNotFoundGracefully() {
        // Run the method under test with a non-existent file path
        assertDoesNotThrow(() -> csvBookLoader.readBookDataFromCsvAndSave("non_existent_file.csv"));

        // Verify that no books were saved
        verify(bookRepository, never()).save(any(Book.class));
    }
}
