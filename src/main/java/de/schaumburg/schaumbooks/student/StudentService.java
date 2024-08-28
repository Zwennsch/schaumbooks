package de.schaumburg.schaumbooks.student;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Transactional
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(long id, Student student) {
        Optional<Student> possibleStudent = studentRepository.findById(id);

        return possibleStudent.map(existingStudent -> {
            existingStudent.setId(id);
            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());
            existingStudent.setClassName(student.getClassName());
            existingStudent.setEmail(student.getEmail());
            return studentRepository.save(existingStudent);
        }).orElseThrow(() -> new StudentNotFoundException(id));

    }
    
    @Transactional
    public void deleteStudentById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.deleteById(student.getId());
        
    }

}
