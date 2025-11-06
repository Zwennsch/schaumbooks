package de.schaumburg.schaumbooks.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.schaumburg.schaumbooks.configuration.SecurityConfig;
import de.schaumburg.schaumbooks.person.CustomPersonDetailsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
public class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @MockBean
    private CustomPersonDetailsService userDetailsService;

    Book updatedBook;

    List<Book> books = new ArrayList<>();

    @BeforeEach
    void setup() {
        books = List.of(new Book(1L, "first book", "first verlag", "123456", BookStatus.AVAILABLE, null),
                new Book(2L, "second book", "second verlag", "654321", BookStatus.MISSING, null));

        updatedBook = new Book(1L, "updated title", "updated verlag", "123-123", BookStatus.AVAILABLE, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFindAllBooks() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "title":"first book",
                        "verlag":"first verlag",
                        "isbn":"123456",
                        "status":"AVAILABLE",
                        "person":null
                    },
                    {
                        "id":2,
                        "title":"second book",
                        "verlag":"second verlag",
                        "isbn":"654321",
                        "status":"MISSING",
                        "person":null
                    }
                ]
                """;
        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        verify(bookService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFindBookByGivenValidId() throws Exception {
        String jsonResponse = """
                {
                    "id":1,
                    "title":"first book",
                    "verlag":"first verlag",
                    "isbn":"123456",
                    "status":"AVAILABLE",
                    "person":null
                    }
                    """;
        when(bookService.findById(1L)).thenReturn(books.get(0));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForId999() throws Exception {
        when(bookService.findById(999L)).thenThrow(new BookNotFoundException(999L));
        String jsonResponse = """
                {
                    "statusCode" : 404,
                    "message": "Book not found with id: 999"
                }
                """;
        mockMvc.perform(get("/api/books/999"))
                .andExpect(content().json(jsonResponse))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAddNewBookWhenBookIsValid() throws Exception {
        Book book = new Book(3L, "new Book", "new Verlag", "123-45", BookStatus.AVAILABLE, null);
        // String jsonObject = asJsonString(book);
        when(bookService.save(book)).thenReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.title").value("new Book"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddNewBookWithInvalidInformation() throws Exception {
        Book invalidBook = new Book();
        invalidBook.setTitle(""); // Invalid because title is empty
        invalidBook.setVerlag("Verlag One");
        invalidBook.setIsbn(""); // Invalid because ISBN is empty
        invalidBook.setStatus(BookStatus.AVAILABLE);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidBook)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddNewBookWithMissingInformation() throws Exception {
        Book missingBook = new Book();
        // Missing title, verlag, and ISBN

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(missingBook)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookValidInput() throws Exception {
        Book book = new Book(1L, "updated title", "updated verlag", "123-123", BookStatus.AVAILABLE, null);

        when(bookService.updateBook(1L, book)).thenReturn(book);

        mockMvc.perform(put("/api/books/1")
                .contentType("application/json")
                .content(asJsonString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("updated title"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShouldGetIsNotFoundIfBookIsInvalidWhenUpdating() throws Exception {
        Book book = new Book(99L, "updated title", "updated verlag", "123-123", BookStatus.AVAILABLE, null);
        when(bookService.updateBook(99L, book)).thenThrow(BookNotFoundException.class);

        mockMvc.perform(put("/api/books/99")
                .contentType("application/json")
                .content(asJsonString(book)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteBookWithValidId() throws Exception {
        doNothing().when(bookService).deleteBookById(1l);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenDeletingNonExistentBook() throws Exception {
        doThrow(new BookNotFoundException(999L)).when(bookService).deleteBookById(999L);

        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
