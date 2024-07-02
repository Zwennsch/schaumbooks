package de.schaumburg.schaumbooks.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.schaumburg.schaumbooks.student.Student;

public class TestBook {

    Student s1 = new Student(1L, "hans", "meier", "9a", "mail@test");
    
    @Test
    void shouldBeEqualWhenComparingIdenticalBooks(){

        Student student1 = s1;

        Book book1 = new Book(1L, "title", "verlag", "123-456", BookStatus.AVAILABLE, student1);
        Book book2 = new Book(1L, "title", "verlag", "123-456", BookStatus.AVAILABLE, student1);
        
        assertEquals(book1, book2);
    }
    
    @Test
    void shouldReturnTrueWhenBookIsLent(){
        
        Book book1 = new Book(1L, "title", "verlag", "123-456", BookStatus.LENT, s1);
        Book book2 = new Book(1L, "title", "verlag", "123-456", BookStatus.AVAILABLE, s1);

        assertTrue(book1.isLend());

        assertFalse(book2.isLend());
    }
}
