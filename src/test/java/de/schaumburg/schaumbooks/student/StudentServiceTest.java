package de.schaumburg.schaumbooks.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class StudentServiceTest {

    // TODO: This should be tested by the service and not by the controller:

    // @Test
    // void shouldAddNewStudentWithId3WhenNoIdIsGiven() throws
    // JsonProcessingException, Exception{
    // // Student studentNoId = new Student(null, "Hans", "Meier", "10a",
    // "test@mail.com");
    // String jsonStringNoId = """
    // {
    // "firstName" : "Hans",
    // "lastName" : "Meier",
    // "className" : "10a",
    // "email" : "test@email.com"
    // }
    // """;
    // mockMvc.perform(post("/api/students")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(jsonStringNoId))
    // .andExpect(status().isCreated())
    // .andExpect(jsonPath("$.id").value(3L))
    // .andExpect(jsonPath("$.firstName").value("Hans"));
    // }

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private List<Student> students;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        Student student1 = new Student(1L, "student1", "lastname1", "8a", "student1@mailcom");
        Student student2 = new Student(2L, "student2", "lastname2", "9a", "student2@mailcom");

        students = List.of(student1, student2);
    }

    // findAll()
    @Test
    void shouldReturnAllStudentsFromRepository() {
        when(studentRepository.findAll()).thenReturn(students);
        List<Student> allStudents = studentService.findAll();

        assertEquals("student2", allStudents.get(1).getFirstName());
        verify(studentRepository, times(1)).findAll();

    }

    // findById
    @Test
    void shouldFindStudentGivenValidId() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(students.get(0)));
        Student student = studentService.findStudentById(1L);

        assertEquals(students.get(0), student);
        verify(studentRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenGivenInvalidId() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.findStudentById(99L));
        verify(studentRepository).findById(99L);
    }

    // save
    @Test
    void shouldAddNewStudentGivenValidInput() {
        Student student = new Student(3L, "Hans", "Meier", "10a", "hans@mail.com");
        when(studentRepository.save(student)).thenReturn(student);

        Student savedStudent = studentService.save(student);
        assertEquals(student, savedStudent);
        verify(studentRepository).save(student);
    }

    // update
    @Test
    void shouldUpdateStudentGivenValidInput() {
        Student updatedStudent = new Student(1L, "newName", "newLastName", "10a", "newMail@mail.com");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(students.get(0)));
        when(studentRepository.save(updatedStudent)).thenReturn(updatedStudent);
        Student result = studentService.updateStudent(1L, updatedStudent);
        assertEquals(updatedStudent, result);
        verify(studentRepository).save(updatedStudent);
    }

    @Test
    void shouldThrowStudentNotFoundExceptionWhenUpdatingWithInvalidId() {
        Student updatedStudent = new Student(99L, "newName", "newLastName", "10a", "newMail@mail.com");
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> studentService.updateStudent(99L, updatedStudent));
    }

    @Test
    void shouldUpdateOnlySpecifiedFields() {
        Student existingStudent = new Student(1L, "John", "Doe", "10a", "john.doe@mail.com");
        Student updatedStudent = new Student(1L, "John", "Doe", "10a", "newMail@mail.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(existingStudent)).thenReturn(updatedStudent);

        Student result = studentService.updateStudent(1L, updatedStudent);

        assertEquals(updatedStudent.getEmail(), result.getEmail());
        assertEquals(existingStudent.getFirstName(), result.getFirstName());
        assertEquals(existingStudent.getLastName(), result.getLastName());
        verify(studentRepository).save(existingStudent);
    }

}
