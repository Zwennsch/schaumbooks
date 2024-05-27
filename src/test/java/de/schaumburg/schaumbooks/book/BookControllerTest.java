package de.schaumburg.schaumbooks.book;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    public void testUpdateBook() {
        // Mock data
        Long bookId = 1L;
        Book updatedBookData = new Book();
        updatedBookData.setTitle("Updated Title");
        updatedBookData.setVerlag("Updated Verlag");
        updatedBookData.setIsbn("Updated ISBN");
        updatedBookData.setStatus(BookStatus.AVAILABLE);

        // Mock service behavior
        Book existingBook = new Book();
        existingBook.setId(bookId);
        when(bookService.updateBook(eq(bookId), any())).thenReturn(Optional.of(existingBook));

        // Call the controller method
        ResponseEntity<Book> response = bookController.updateBook(bookId, updatedBookData);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingBook, response.getBody());
    }

    // @Test
    // public void test 
}
