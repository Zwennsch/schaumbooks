package de.schaumburg.schaumbooks.book;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;



@Entity
public record Book(
        @Id
        Long id,
        @NotEmpty String title,
        @NotEmpty String author) {

}
