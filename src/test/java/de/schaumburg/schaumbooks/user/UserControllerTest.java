package de.schaumburg.schaumbooks.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.schaumburg.schaumbooks.configuration.TestSecurityConfig;
import de.schaumburg.schaumbooks.user.Role;
import de.schaumburg.schaumbooks.user.User;
import de.schaumburg.schaumbooks.user.UserController;
import de.schaumburg.schaumbooks.user.UserNotFoundException;
import de.schaumburg.schaumbooks.user.UserService;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    List<User> users = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {

        users = List.of(new User(1l, "user1", "1234", "Test1", "Test1", "test@email.com", List.of(Role.STUDENT), "8a"),
                new User(2l, "user2", "1234", "Test2", "Test2", "test2@email.com", List.of(Role.STUDENT), "8b"));
        new User(4l, "user3", "1234", "Horst", "admin", "admin@email.com", List.of(Role.ADMIN), null);
        new User(3l, "user4", "1234", "Hans", "teacher", "teacher1@email.com", List.of(Role.TEACHER), null);
    }

    // GET: findAll
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFindAllStudents() throws Exception {
        String jsonString = objectMapper.writeValueAsString(users);

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(jsonString));

        verify(userService).findAll();

    }

    // GET: findById
    @Test
    void shouldFindUserGivenValidId() throws Exception {
        String jsonResponse = objectMapper.writeValueAsString(users.get(0));

        when(userService.findUserById(1L)).thenReturn(users.get(0));

        mockMvc.perform(get("/api/users/1")
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForId999() throws Exception {
        when(userService.findUserById(999L)).thenThrow(new UserNotFoundException(999L));

        String jsonResponse = """
                {
                    "statusCode" : 404,
                    "message": "User not found with id: 999"
                }
                """;

        mockMvc.perform(get("/api/users/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldFindUserGivenUsername() throws Exception {
        // Given
        String username = "user1";
        String jsonResponse = objectMapper.writeValueAsString(users.get(0));
        // When
        when(userService.findUserByUsername(username)).thenReturn(users.get(0));
        // Then
        mockMvc.perform(get("/api/users/username/" + username)
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

    }

    // POST: addStudent
    @Test
    void shouldAddNewStudentGivenValidStudent() throws Exception {
        User student = new User(5L, "user5", "1234", "Hans", "Meier", "test@mail.com", List.of(Role.STUDENT), "8b");

        when(userService.save(student)).thenReturn(student);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student))
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.firstName").value("Hans"));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidStudents")
    public void shouldReturnBadRequestForInvalidStudents(User invalidStudent) throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidStudent))
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideInvalidStudents() {
        return Stream.of(
                Arguments.of(new User(null, "user20", "1234", "", "Doe", "john.doe@example.com", List.of(Role.STUDENT),
                        "10a")), // empty
                // firstName
                Arguments.of(new User(null, "user21", "1234", "John", "", "john.doe@example.com", List.of(Role.STUDENT),
                        "10b")), // empty
                // lastName
                // Arguments.of(new User(null, "John", "Doe", "john.doe@example.com",
                // List.of(Role.STUDENT), "")), // empty
                // className
                // for
                // student
                Arguments.of(new User(null, "user22", "1234", "John", "Doe", "", List.of(Role.STUDENT), "10A")), // empty
                                                                                                                 // email
                Arguments.of(
                        new User(null, "user23", "1234", "John", "Doe", "not-an-email", List.of(Role.STUDENT), "10A")) // invalid
        // email
        );
    }

    // PUT: updateStudent()
    @Test
    void shouldUpdateStudentGivenValidInput() throws JsonProcessingException, Exception {
        User student = new User(1L, "user1", "1234", "updatedName", "Test", "Hans@email.com", List.of(Role.STUDENT),
                "8b");

        when(userService.updateUser(1l, student)).thenReturn(student);

        mockMvc.perform(put("/api/users/1")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(student))
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("updatedName"));
    }

    @Test
    void shouldGetStudentNotFoundGivenInvalidId() throws JsonProcessingException, Exception {
        User student = new User(999L, "user999", "1234", "updatedName", "Test", "Hans@email.com", List.of(Role.STUDENT),
                "8b");

        when(userService.updateUser(999L, student)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(put("/api/users/999")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(student))
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenUpdatingStudentWithInvalidData() throws Exception {
        // Create a Student object with invalid data (e.g., missing required fields)
        User invalidUser = new User();
        invalidUser.setId(1L); // Assuming ID is set for an update, but fields are empty
        invalidUser.setFirstName(""); // Invalid because @NotEmpty should trigger validation error
        invalidUser.setLastName(""); // Invalid because @NotEmpty should trigger validation error
        invalidUser.setClassName(""); // Invalid because @NotEmpty should trigger validation error
        invalidUser.setEmail("invalidEmail"); // Invalid because it doesn't match the email pattern

        // Perform a PUT request with invalid data
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser))
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                .andExpect(jsonPath("$.firstName").value("must not be empty"))
                .andExpect(jsonPath("$.lastName").value("must not be empty"))
                // .andExpect(jsonPath("$.className").value("must not be empty"))
                .andExpect(jsonPath("$.email").value("must be a well-formed email address"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPatchUserFieldsSuccessfully() throws JsonProcessingException, Exception {
        // Given
        Map<String, Object> updateFields = Map.of(
                "firstName", "NewName",
                "email", "new.email@example.com"

        );
        User updatedUser = new User(1l, "user1", "1234", "NewName", "Test1", "new.email@example.com",
                List.of(Role.STUDENT), "8a");
        // When
        when(userService.updateUserFields(eq(1L), any(Map.class))).thenReturn(updatedUser);
        // When/Then
        mockMvc.perform(patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateFields))
                // .with(httpBasic("sven", "1234"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewName"))
                .andExpect(jsonPath("$.email").value("new.email@example.com"));

        verify(userService, times(1)).updateUserFields(eq(1L), any(Map.class));
    }

    // TODO: test for failing patch request

    // DELETE: deleteStudentById()
    @Test
    void shouldDeleteStudentWhenGivenValidId() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/api/users/1")
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingStudent() throws Exception {
        doThrow(new UserNotFoundException(999L)).when(userService).deleteUserById(999L);

        mockMvc.perform(delete("/api/users/999")
                .with(httpBasic("sven", "1234")))
                .andExpect(status().isNotFound());
    }

}
