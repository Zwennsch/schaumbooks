package de.schaumburg.schaumbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.schaumburg.schaumbooks.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    
}
