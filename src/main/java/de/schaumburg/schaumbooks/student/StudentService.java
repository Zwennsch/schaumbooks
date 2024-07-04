package de.schaumburg.schaumbooks.student;

import java.util.List;

import org.springframework.stereotype.Service;



@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public List<Student> findAll() {
        return studentRepository.findAll();
    }


    Student findStudentById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }


    public Student save(Student student) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    
}
