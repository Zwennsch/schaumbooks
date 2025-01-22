package de.schaumburg.schaumbooks.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.schaumburg.schaumbooks.user.Role;
import de.schaumburg.schaumbooks.user.User;

public class TestBook {

    User s1 = new User(1L, "hans", "meier", "mail@test", List.of(Role.STUDENT), "9a");
    
    @Test
    void shouldBeEqualWhenComparingIdenticalBooks(){

        User student1 = s1;

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
