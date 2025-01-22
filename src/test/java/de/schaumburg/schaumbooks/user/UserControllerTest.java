// package de.schaumburg.schaumbooks.user;

// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Stream;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.Arguments;
// import org.junit.jupiter.params.provider.MethodSource;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import de.schaumburg.schaumbooks.configuration.TestSecurityConfig;
// import de.schaumburg.schaumbooks.user.Role;
// import de.schaumburg.schaumbooks.user.User;
// import de.schaumburg.schaumbooks.user.UserController;
// import de.schaumburg.schaumbooks.user.UserNotFoundException;
// import de.schaumburg.schaumbooks.user.UserService;

// @WebMvcTest(UserController.class)
// @Import(TestSecurityConfig.class)
// public class UserControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private UserService studentService;

//     List<User> students = new ArrayList<>();
//     ObjectMapper objectMapper = new ObjectMapper();

//     @BeforeEach
//     public void setup() {

//         students = List.of(new User(1l, "Test1", "Test1", "test@email.com", List.of(Role.STUDENT), "8a"),
//                 new User(2l, "Test2", "Test2", "test2@email.com", List.of(Role.STUDENT), "8b"));
//     }

//     // GET: findAll
//     @Test
//     void shouldFindAllStudents() throws Exception {
//         String jsonString = objectMapper.writeValueAsString(students);

//         when(studentService.findAll()).thenReturn(students);

//         mockMvc.perform(get("/api/students")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json(jsonString));

//         verify(studentService).findAll();

//     }

//     // GET: findById
//     @Test
//     void shouldFindBookGivenValidId() throws Exception {
//         String jsonResponse = objectMapper.writeValueAsString(students.get(0));

//         when(studentService.findUserById(1L)).thenReturn(students.get(0));

//         mockMvc.perform(get("/api/students/1")
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isOk())
//                 .andExpect(content().json(jsonResponse));
//     }

//     @Test
//     void shouldReturnNotFoundForId999() throws Exception {
//         when(studentService.findUserById(999L)).thenThrow(new UserNotFoundException(999L));

//         String jsonResponse = """
//                 {
//                     "statusCode" : 404,
//                     "message": "Student not found with id: 999"
//                 }
//                 """;

//         mockMvc.perform(get("/api/students/999")
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isNotFound())
//                 .andExpect(content().json(jsonResponse));
//     }

//     // POST: addStudent
//     @Test
//     void shouldAddNewStudentGivenValidStudent() throws Exception {
//         User student = new User(3L, "Hans", "Meier", "10a", "test@mail.com");

//         when(studentService.save(student)).thenReturn(student);

//         mockMvc.perform(post("/api/students")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(student))
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.id").value(3L))
//                 .andExpect(jsonPath("$.firstName").value("Hans"));
//     }

//     @ParameterizedTest
//     @MethodSource("provideInvalidStudents")
//     public void shouldReturnBadRequestForInvalidStudents(User invalidStudent) throws Exception {
//         mockMvc.perform(post("/api/students")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(new ObjectMapper().writeValueAsString(invalidStudent))
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isBadRequest());
//     }

//     private static Stream<Arguments> provideInvalidStudents() {
//         return Stream.of(
//                 Arguments.of(new User(null, "", "Doe", "10A", "john.doe@example.com")), // empty firstName
//                 Arguments.of(new User(null, "John", "", "10A", "john.doe@example.com")), // empty lastName
//                 Arguments.of(new User(null, "John", "Doe", "", "john.doe@example.com")), // empty className
//                 Arguments.of(new User(null, "John", "Doe", "10A", "")), // empty email
//                 Arguments.of(new User(null, "John", "Doe", "10A", "not-an-email")) // invalid email
//         );
//     }

//     // PUT: updateStudent()
//     @Test
//     void shouldUpdateStudentGivenValidInput() throws JsonProcessingException, Exception {
//         User student = new User(1L, "updatedName", "Test", "8b", "Hans@email.com");

//         when(studentService.updateUser(1l, student)).thenReturn(student);

//         mockMvc.perform(put("/api/students/1")
//                 .contentType("application/json")
//                 .content(new ObjectMapper().writeValueAsString(student))
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.firstName").value("updatedName"));
//     }

//     @Test
//     void shouldGetStudentNotFoundGivenInvalidId() throws JsonProcessingException, Exception {
//         User student = new User(999L, "updatedName", "Test", "8b", "Hans@email.com");

//         when(studentService.updateUser(999L, student)).thenThrow(UserNotFoundException.class);

//         mockMvc.perform(put("/api/students/999")
//                 .contentType("application/json")
//                 .content(new ObjectMapper().writeValueAsString(student))
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void shouldReturn400WhenUpdatingStudentWithInvalidData() throws Exception {
//         // Create a Student object with invalid data (e.g., missing required fields)
//         User invalidStudent = new User();
//         invalidStudent.setId(1L); // Assuming ID is set for an update, but fields are empty
//         invalidStudent.setFirstName(""); // Invalid because @NotEmpty should trigger validation error
//         invalidStudent.setLastName(""); // Invalid because @NotEmpty should trigger validation error
//         invalidStudent.setClassName(""); // Invalid because @NotEmpty should trigger validation error
//         invalidStudent.setEmail("invalidEmail"); // Invalid because it doesn't match the email pattern

//         // Perform a PUT request with invalid data
//         mockMvc.perform(put("/api/students/1")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(invalidStudent))
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
//                 .andExpect(jsonPath("$.firstName").value("must not be empty"))
//                 .andExpect(jsonPath("$.lastName").value("must not be empty"))
//                 .andExpect(jsonPath("$.className").value("must not be empty"))
//                 .andExpect(jsonPath("$.email").value("must be a well-formed email address"));
//     }

//     // DELETE: deleteStudentById()
//     @Test
//     void shouldDeleteStudentWhenGivenValidId() throws Exception {
//         doNothing().when(studentService).deleteUserById(1L);

//         mockMvc.perform(delete("/api/students/1")
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isNoContent());
//     }

//     @Test
//     void shouldReturn404WhenDeletingNonExistingStudent() throws Exception {
//         doThrow(new UserNotFoundException(999L)).when(studentService).deleteUserById(999L);

//         mockMvc.perform(delete("/api/students/999")
//                 .with(httpBasic("sven", "1234")))
//                 .andExpect(status().isNotFound());
//     }

// }
