package de.schaumburg.schaumbooks.student;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public class StudentServiceTest {
    
    // TODO: This should be tested by the service and not by the controller:
    
    // @Test
    // void shouldAddNewStudentWithId3WhenNoIdIsGiven() throws JsonProcessingException, Exception{
    //     // Student studentNoId = new Student(null,  "Hans", "Meier", "10a", "test@mail.com");
    //     String jsonStringNoId = """
    //             {
    //                 "firstName" : "Hans",
    //                 "lastName"  : "Meier",
    //                 "className" : "10a",
    //                 "email"     : "test@email.com"
    //             }
    //             """;
    //     mockMvc.perform(post("/api/students")
    //     .contentType(MediaType.APPLICATION_JSON)
    //     .content(jsonStringNoId))
    //     .andExpect(status().isCreated())
    //     .andExpect(jsonPath("$.id").value(3L))
    //     .andExpect(jsonPath("$.firstName").value("Hans"));
    // }
}
