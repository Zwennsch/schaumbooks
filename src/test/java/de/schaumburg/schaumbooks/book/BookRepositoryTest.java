package de.schaumburg.schaumbooks.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.schaumburg.schaumbooks.user.Role;
import de.schaumburg.schaumbooks.user.User;
import de.schaumburg.schaumbooks.user.UserRepository;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private User student;
    private Book book1, book2;

    @BeforeEach
    void setUp() {
        student = new User(1L, "user1", "1234", "student1", "lastname1", "student1@mail.com", List.of(Role.STUDENT),
                "8a");
        userRepository.save(student);

        book1 = new Book(null, "Book One", "Publisher One", "ISBN-12345", BookStatus.LENT, student);
        book2 = new Book(null, "Book Two", "Publisher Two", "ISBN-67890", BookStatus.LENT, student);
        bookRepository.saveAll(List.of(book1, book2));
    }

    @Test
    void shouldFindBooksByUser() {
        List<Book> rentedBooks = bookRepository.findByUser(student);

        assertEquals(2, rentedBooks.size());
        assertTrue(rentedBooks.contains(book1));
        assertTrue(rentedBooks.contains(book2));
    }
}
