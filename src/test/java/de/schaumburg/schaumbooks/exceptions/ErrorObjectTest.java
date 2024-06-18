package de.schaumburg.schaumbooks.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class ErrorObjectTest {
    @Test
    void shouldCreateErrorObject(){
        ErrorObject object = new ErrorObject(Integer.valueOf(404), "Test", new Date());
        assertEquals("Test", object.message());
    }
}
