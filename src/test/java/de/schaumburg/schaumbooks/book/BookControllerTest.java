package de.schaumburg.schaumbooks.book;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    List<Book> books = new ArrayList<>();

    @BeforeEach
    void setup() {
        books = List.of(new Book(1L, "first book", "first verlag", "123456", BookStatus.AVAILABLE, null),
                new Book(2L, "second book", "second verlag", "654321", BookStatus.MISSING, null));
    }

    @Test
    void shouldFindAllBooks() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "title":"first book",
                        "verlag":"first verlag",
                        "isbn":"123456",
                        "status":"AVAILABLE",
                        "student":null
                    },
                    {
                        "id":2,
                        "title":"second book",
                        "verlag":"second verlag",
                        "isbn":"654321",
                        "status":"MISSING",
                        "student":null
                    }
                ]
                """;
        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/api/books/"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    // @InjectMocks
    // private BookController bookController;

    // @Test
    // public void testUpdateBook() {
    // // Mock data
    // Long bookId = 1L;
    // Book updatedBookData = new Book();
    // updatedBookData.setTitle("Updated Title");
    // updatedBookData.setVerlag("Updated Verlag");
    // updatedBookData.setIsbn("Updated ISBN");
    // updatedBookData.setStatus(BookStatus.AVAILABLE);

    // // Mock service behavior
    // Book existingBook = new Book();
    // existingBook.setId(bookId);
    // when(bookService.updateBook(eq(bookId),
    // any())).thenReturn(Optional.of(existingBook));

    // // Call the controller method
    // ResponseEntity<Book> response = bookController.updateBook(bookId,
    // updatedBookData);

    // // Verify the response
    // assertEquals(HttpStatus.OK, response.getStatusCode());
    // assertEquals(existingBook, response.getBody());
    // }

    // // @Test
    // // public void test
}
