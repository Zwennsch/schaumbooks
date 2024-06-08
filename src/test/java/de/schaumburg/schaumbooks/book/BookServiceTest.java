// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import java.util.Optional;

// public class BookServiceTest {

//     @Mock
//     private BookRepository bookRepository;

//     @InjectMocks
//     private BookService bookService;

//     private Book book;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.initMocks(this);
//         book = new Book();
//         book.setTitle("Sample Book");
//         book.setVerlag("Sample Verlag");
//         book.setIsbn("1234567890");
//         book.setStatus(BookStatus.AVAILABLE);
//         book.setStudent(null);
//     }

//     @Test
//     void testUpdateBookWithValidData() {
//         when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//         when(bookRepository.save(any(Book.class))).thenReturn(book);

//         Book updatedBook = new Book();
//         updatedBook.setTitle("Updated Title");
//         updatedBook.setVerlag("Updated Verlag");
//         updatedBook.setIsbn("0987654321");
//         updatedBook.setStatus(BookStatus.LENT);
//         updatedBook.setStudent(new Student());

//         Optional<Book> result = bookService.updateBook(1L, updatedBook);

//         assertTrue(result.isPresent());
//         assertEquals("Updated Title", result.get().getTitle());
//         assertEquals("Updated Verlag", result.get().getVerlag());
//         assertEquals("0987654321", result.get().getIsbn());
//         assertEquals(BookStatus.LENT, result.get().getStatus());
//         assertNotNull(result.get().getStudent());
//     }

//     @Test
//     void testUpdateBookWithInvalidId() {
//         when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

//         Book updatedBook = new Book();
//         updatedBook.setTitle("Updated Title");

//         Optional<Book> result = bookService.updateBook(999L, updatedBook);

//         assertFalse(result.isPresent());
//     }
// }
