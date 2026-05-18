package de.schaumburg.schaumbooks.book;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import de.schaumburg.schaumbooks.person.PersonRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class BookService {

    private final BookRepository bookRepository;

    // private final StudentRepository studentRepository;

    public BookService(BookRepository bookRepository, PersonRepository personRepository) {
        this.bookRepository = bookRepository;
        // this.studentRepository = studentRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public Book save(@NotNull @Valid Book book) {
        Objects.requireNonNull(book);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(@Min(1) long id, @NotNull @Valid Book updatedBook) {
        Objects.requireNonNull(updatedBook);
        Optional<Book> optionalBook = bookRepository.findById(id);

        return optionalBook.map(existingBook -> {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setVerlag(updatedBook.getVerlag());
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setStatus(updatedBook.getStatus());
            existingBook.setPerson(updatedBook.getPerson());
            return bookRepository.save(existingBook);
        }).orElseThrow(() -> new BookNotFoundException(id));
    }

    public void deleteBookById(long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.deleteById(id);
        } else {
            throw new BookNotFoundException(id);
        }
    }

}
