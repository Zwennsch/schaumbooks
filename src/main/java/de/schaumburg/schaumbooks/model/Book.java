package de.schaumburg.schaumbooks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public record Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id,
    String title,
    String author
) {
    
}
