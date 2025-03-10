package de.schaumburg.schaumbooks.book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.schaumburg.schaumbooks.user.User;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByUser(User user);

}
