package de.schaumburg.schaumbooks.student;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;


    List<Student> students = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup(){

        students = List.of(new Student(1l, "Test1", "Test1", "8a", "test@email.com"),
        new Student(2l, "Test2", "Test2", "8b", "test2@email.com"));
    }

    @Test
    void shouldFindAllStudents() throws Exception{
        String jsonString = objectMapper.writeValueAsString(students);

        when(studentService.findAll()).thenReturn(students);

        mockMvc.perform(get("/api/students")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(jsonString));

        verify(studentService).findAll();

    }

    @Test
    void shouldFindBookGivenValidId() throws Exception{
        String jsonResponse = objectMapper.writeValueAsString(students.get(0));

        when(studentService.findStudentById(1L)).thenReturn(students.get(0));

        mockMvc.perform(get("/api/students/1"))
            .andExpect(status().isOk())
            .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldReturnNotFoundForId999() throws Exception{
        when(studentService.findStudentById(999L)).thenThrow(new StudentNotFoundException(999L));

        String jsonResponse = """
                {
                    "statusCode" : 404,
                    "message": "Student not found with id: 999"
                }
                """;

        mockMvc.perform(get("/api/students/999"))
            .andExpect(status().isNotFound())
            .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldAddNewStudentGivenValidStudent() throws Exception {
        Student student = new Student(3L, "Hans", "Meier", "10a", "test@mail.com");
        
        when(studentService.save(student)).thenReturn(student);
        
        mockMvc.perform(post("/api/students")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(student)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(3L))
        .andExpect(jsonPath("$.firstName").value("Hans"));
    }
    
    @ParameterizedTest
    @MethodSource("provideInvalidStudents")
    public void shouldReturnBadRequestForInvalidStudents(Student invalidStudent) throws Exception {
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidStudents() {
        return Stream.of(
                Arguments.of(new Student(null, "", "Doe", "10A", "john.doe@example.com")),  // empty firstName
                Arguments.of(new Student(null, "John", "", "10A", "john.doe@example.com")),  // empty lastName
                Arguments.of(new Student(null, "John", "Doe", "", "john.doe@example.com")),  // empty className
                Arguments.of(new Student(null, "John", "Doe", "10A", "")),  // empty email
                Arguments.of(new Student(null, "John", "Doe", "10A", "not-an-email"))  // invalid email
        );
    }
    
    
    



    
}
