package de.schaumburg.schaumbooks.book;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import de.schaumburg.schaumbooks.student.StudentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class BookService {

    private final BookRepository bookRepository;
    
    private final StudentRepository studentRepository;
    
    public BookService(BookRepository bookRepository, StudentRepository studentRepository) {
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book save(@Valid Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Optional<Book> updateBook(@NotNull @Min(1) Long id, @Valid Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(id);

        return optionalBook.map(existingBook -> {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setVerlag(updatedBook.getVerlag());
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setStatus(updatedBook.getStatus());
            existingBook.setStudent(updatedBook.getStudent());
            return bookRepository.save(existingBook);
        });
    }



    public void deleteBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent()){
            bookRepository.deleteById(id);
        }else {
            throw new BookNotFoundException(id);
        }
    }

}
