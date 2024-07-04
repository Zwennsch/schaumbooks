package de.schaumburg.schaumbooks.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.schaumburg.schaumbooks.book.BookNotFoundException;
import de.schaumburg.schaumbooks.student.StudentNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorObject> handleBookNotFoundException(BookNotFoundException exception) {
        ErrorObject errorObject = new ErrorObject(HttpStatus.NOT_FOUND.value(), exception.getMessage(), new Date());

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorObject> handleStudentNotFoundException(StudentNotFoundException exception){
        ErrorObject errorObject = new ErrorObject(HttpStatus.NOT_FOUND.value(), exception.getMessage(), new Date());

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.NOT_FOUND);
    }

}


