package de.schaumburg.schaumbooks.exceptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.schaumburg.schaumbooks.book.BookNotFoundException;
import de.schaumburg.schaumbooks.person.InvalidPersonInputException;
import de.schaumburg.schaumbooks.person.PersonNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorObject> handleBookNotFoundException(BookNotFoundException exception) {
        ErrorObject errorObject = new ErrorObject(HttpStatus.NOT_FOUND.value(), exception.getMessage(), new Date());

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorObject> handleUserNotFoundException(PersonNotFoundException exception) {
        ErrorObject errorObject = new ErrorObject(HttpStatus.NOT_FOUND.value(), exception.getMessage(), new Date());

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPersonInputException.class)
    public ResponseEntity<ErrorObject> handleInvalidUserInputException(InvalidPersonInputException exception) {
        ErrorObject errorObject = new ErrorObject(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), new Date());
        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
