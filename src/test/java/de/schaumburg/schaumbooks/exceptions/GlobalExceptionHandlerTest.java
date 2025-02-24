package de.schaumburg.schaumbooks.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.schaumburg.schaumbooks.user.InvalidUserInputException;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setup(){
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleInvalidUserInputException() {
        // Given 
        InvalidUserInputException exception = new InvalidUserInputException("Student must have a class name");

        // When
        ResponseEntity<ErrorObject> response = exceptionHandler.handleInvalidUserInputException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().statusCode());
        assertEquals("Invalid user input: Student must have a class name", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }
}
