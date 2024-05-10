package de.schaumburg.schaumbooks.book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.schaumburg.schaumbooks.student.StudentRepository;

@Service
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

    public Optional<Book> updateBook(Long id, Book updatedBook){
        Optional<Book> optionalBook = bookRepository.findById(id);
        if(optionalBook.isPresent()){
            Book existingBook = optionalBook.get();
            // Update Book with new data
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setStatus(updatedBook.getStatus());
            existingBook.setStudent(null);
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setVerlag(updatedBook.getVerlag());
            return Optional.of(bookRepository.save(existingBook));
        }else{
            return Optional.empty();
        }
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException());
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

}
