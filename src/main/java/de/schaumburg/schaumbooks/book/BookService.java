package de.schaumburg.schaumbooks.book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public void readDataFromCsvAndSave(String csvFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip first line:
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String title = parts[0].trim();
                String verlag = parts[1].trim();
                String isbn = parts[2].trim();
                int numOfBooks = Integer.parseInt(parts[3].trim());
                for (int i = 0; i < numOfBooks; i++) {
                    Book book = new Book();
                    book.setTitle(title);
                    book.setVerlag(verlag);
                    book.setIsbn(isbn);
                    bookRepository.save(book);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not find csv-file " + e.getMessage());
        }
    }
    @Transactional
    public Optional<Book> updateBook(@NotNull @Min(1) Long id, @Valid Book updatedBook){
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

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(bookRepository.findById(id).orElseThrow(BookNotFoundException::new));
    }

    @Transactional
    public Book save(@Valid Book book) {
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

}
