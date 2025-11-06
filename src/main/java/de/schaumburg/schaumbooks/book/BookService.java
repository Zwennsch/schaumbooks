package de.schaumburg.schaumbooks.book;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import de.schaumburg.schaumbooks.person.PersonRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// TODO: This was added in a tutorial I am not sure I would need it.
// @Transactional
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

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public Book save(@Valid Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(@NotNull @Min(1) Long id, @Valid Book updatedBook) {
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

    public void deleteBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.deleteById(id);
        } else {
            throw new BookNotFoundException(id);
        }
    }

}
