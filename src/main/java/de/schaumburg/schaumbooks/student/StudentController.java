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

    // @GetMapping("/{id}")
    // public Student findById(@PathVariable Long id) {
    //     return StudentService.findById(id)
    //             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No student found with id"));
    // }

    // @ResponseStatus(HttpStatus.CREATED)
    // @PostMapping("")
    // public void create(@Valid @RequestBody Student student) {
    //     StudentService.save(student);
    // }

}
