package de.schaumburg.schaumbooks.student;

public class StudentNotFoundException extends RuntimeException{
    public StudentNotFoundException(Long id){
        super("Student not found with id: "+id);
    }
    
}