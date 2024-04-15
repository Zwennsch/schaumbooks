package de.schaumburg.schaumbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.schaumburg.schaumbooks.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long>{
    
}
