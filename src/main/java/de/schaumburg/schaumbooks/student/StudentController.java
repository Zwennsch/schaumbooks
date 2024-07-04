package de.schaumburg.schaumbooks.student;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("")
    public ResponseEntity<List<Student>> findAll() {
        List<Student> allStudents = studentService.findAll();
        return ResponseEntity.ok(allStudents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> findStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.findStudentById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ResponseEntity<Student> addStudent(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.save(student));
    }

}
