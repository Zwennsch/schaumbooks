package de.schaumburg.schaumbooks.book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.schaumburg.schaumbooks.person.Person;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByPerson(Person user);

}
