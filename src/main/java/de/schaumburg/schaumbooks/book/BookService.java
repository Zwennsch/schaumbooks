package de.schaumburg.schaumbooks.book;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.schaumburg.schaumbooks.student.StudentRepository;

@Service
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

    // TODO: Create BookNotFundExcpetion
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Could not find Book with id of " + id));
    }

}
