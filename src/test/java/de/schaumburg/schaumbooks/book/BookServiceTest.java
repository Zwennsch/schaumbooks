package de.schaumburg.schaumbooks.book;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

// import de.schaumburg.schaumbooks.student.Student;

import java.util.Optional;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private List<Book> books;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Book book1 = new Book();
        book1.setTitle("Sample Book");
        book1.setVerlag("Sample Verlag");
        book1.setIsbn("1234567890");
        book1.setStatus(BookStatus.AVAILABLE);
        book1.setStudent(null);

        Book book2 = new Book();
        book2.setTitle("Sample Book2");
        book2.setVerlag("Sample Verlag2");
        book2.setIsbn("1234567890-2");
        book2.setStatus(BookStatus.LENT);
        book2.setStudent(null);

        books = List.of(book1, book2);
    }

    @Test
    void testUpdateBookWithValidData() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(books.get(0)));
        when(bookRepository.save(any(Book.class))).thenReturn(books.get(0));

        Book updatedBook = new Book();
        updatedBook.setTitle("Updated Title");
        updatedBook.setVerlag("Updated Verlag");
        updatedBook.setIsbn("0987654321");
        updatedBook.setStatus(BookStatus.LENT);
        updatedBook.setStudent(null);

        Optional<Book> result = bookService.updateBook(1L, updatedBook);

        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals("Updated Verlag", result.get().getVerlag());
        assertEquals("0987654321", result.get().getIsbn());
        assertEquals(BookStatus.LENT, result.get().getStatus());
    }

    @Test
    void shouldCallFindAllCorrectly(){
        when(bookRepository.findAll()).thenReturn(books);
        List<Book> allBooks = bookService.findAll();

        assertEquals("Sample Book", allBooks.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldFindBookById(){
        when(bookRepository.findById(1l)).thenReturn(Optional.of(books.get(0)));
        Optional<Book> book = bookService.findById(1l);

        assertEquals(books.get(0).getTitle(), book.get().getTitle());
        verify(bookRepository, times(1)).findById(1l);
    }


    @Test
    void testUpdateBookWithInvalidId() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Book updatedBook = new Book();
        updatedBook.setTitle("Updated Title");

        Optional<Book> result = bookService.updateBook(999L, updatedBook);

        assertFalse(result.isPresent());
    }
}
