package de.schaumburg.schaumbooks.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import de.schaumburg.schaumbooks.person.Role;
import de.schaumburg.schaumbooks.person.Person;
import de.schaumburg.schaumbooks.person.PersonRepository;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PersonRepository userRepository;

    private Person student;
    private Book book1, book2;

    @BeforeEach
    void setUp() {
        // create user without pre-set id and persist it, assigning the managed instance
        // back
        student = new Person(null, "user1", "1234", "student1", "lastname1", "student1@mail.com", List.of(Role.STUDENT),
                "8a");
        student = userRepository.save(student);

        book1 = new Book(null, "Book One", "Publisher One", "ISBN-12345", BookStatus.LENT, student);
        book2 = new Book(null, "Book Two", "Publisher Two", "ISBN-67890", BookStatus.LENT, student);
        bookRepository.saveAll(List.of(book1, book2));
    }

    @Test
    void shouldFindBooksByUser() {
        List<Book> rentedBooks = bookRepository.findByPerson(student);

        assertEquals(2, rentedBooks.size());
        assertTrue(rentedBooks.contains(book1));
        assertTrue(rentedBooks.contains(book2));
    }
}
