package de.schaumburg.schaumbooks.book;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id){
        super("Book not found with id: "+ id);
    }
}
