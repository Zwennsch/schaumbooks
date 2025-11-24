package de.schaumburg.schaumbooks.person;

// import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

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

import de.schaumburg.schaumbooks.book.Book;
import de.schaumburg.schaumbooks.book.BookStatus;
import de.schaumburg.schaumbooks.configuration.SecurityConfig;

@WebMvcTest(PersonController.class)
@Import(SecurityConfig.class)
public class PersonControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PersonService userService;

        @MockBean
        private de.schaumburg.schaumbooks.person.CustomPersonDetailsService userDetailsService;

        List<Person> users = new ArrayList<>();
        List<Book> rentedBooks;
        ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        public void setup() {
                // setup four users: 1 ->Student ; 2 -> Student; 3->Admin; 4->Teacher
                users = List.of(new Person(1l, "user1", "1234", "Test1", "Test1", "test@email.com",
                                List.of(Role.STUDENT),
                                "8a"),
                                new Person(2l, "user2", "1234", "Test2", "Test2", "test2@email.com",
                                                List.of(Role.STUDENT), "8b"));
                new Person(4l, "user3", "1234", "Horst", "admin", "admin@email.com", List.of(Role.ADMIN), null);
                new Person(3l, "user4", "1234", "Hans", "teacher", "teacher1@email.com", List.of(Role.TEACHER), null);

                // Books id=1 and id=2 are rented by Student with id 1; Book 3 is rented by Student id2
                rentedBooks = List.of(
                                new Book(1l, "Book one", "Verlag 1", "123-4567", BookStatus.LENT, users.get(0)),
                                new Book(2l, "Book two", "Verlag 2", "123-4568", BookStatus.LENT, users.get(0)),
                                new Book(3l, "Book three", "Verlag 3", "123-4569", BookStatus.LENT, users.get(1)));
        }

        // CREATE
        // POST: addStudent
        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldAddNewStudentGivenValidStudent() throws Exception {
                Person student = new Person(5L, "user5", "1234", "Hans", "Meier", "test@mail.com",
                                List.of(Role.STUDENT),
                                "8b");

                when(userService.save(student)).thenReturn(student);

                mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student))).andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(5L)).andExpect(jsonPath("$.firstName").value("Hans"));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidStudents")
        @WithMockUser(roles = "ADMIN")
        public void shouldReturnBadRequestForInvalidStudents(Person invalidStudent) throws Exception {
                mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(invalidStudent)))
                                .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideInvalidStudents() {
                return Stream.of(Arguments.of(new Person(null, "user20", "1234", "", "Doe", "john.doe@example.com",
                                List.of(Role.STUDENT), "10a")),
                                // empty firstName
                                Arguments.of(new Person(null, "user21", "1234", "", "lastName", "john.doe@example.com",
                                                List.of(Role.STUDENT), "10b")),
                                // empty lastName
                                Arguments.of(new Person(null, "user22", "1234", "John", "", "john.doe@example.com",
                                                List.of(Role.STUDENT), "10b")),
                                // invalid email
                                Arguments.of(new Person(null, "user23", "1234", "John", "Doe", "not-an-email",
                                                List.of(Role.STUDENT), "10A")), 
                                // empty email
                                Arguments.of(new Person(null, "user24", "1234", "John", "Doe", "",
                                                List.of(Role.STUDENT), "10A"))
                );
        }

        // READ
        // GET: findAll
        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldFindAllStudents() throws Exception {
                String jsonString = objectMapper.writeValueAsString(users);

                when(userService.findAll()).thenReturn(users);

                mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                                .andExpect(content().json(jsonString));

                verify(userService).findAll();

        }

        // GET: findById
        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldFindUserGivenValidId() throws Exception {
                String jsonResponse = objectMapper.writeValueAsString(users.get(0));

                when(userService.findPersonById(1L)).thenReturn(users.get(0));

                mockMvc.perform(get("/api/users/1")).andExpect(status().isOk()).andExpect(content().json(jsonResponse));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnNotFoundForId999() throws Exception {
                when(userService.findPersonById(999L)).thenThrow(new PersonNotFoundException(999L));

                String jsonResponse = """
                                {
                                    "statusCode" : 404,
                                    "message": "Person not found with id: 999"
                                }
                                """;

                mockMvc.perform(get("/api/users/999")).andExpect(status().isNotFound())
                                .andExpect(content().json(jsonResponse));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldFindUserGivenUsername() throws Exception {
                // Given
                String username = "user1";
                String jsonResponse = objectMapper.writeValueAsString(users.get(0));
                // When
                when(userService.findPersonByUsername(username)).thenReturn(users.get(0));
                // Then
                mockMvc.perform(get("/api/users/username/" + username)).andExpect(status().isOk())
                                .andExpect(content().json(jsonResponse));

        }

        // TODO: This test should also test for correctly returned books
        @Test
        @WithMockUser(username = "user1", roles = { "STUDENT" })
        void studentShouldAccessOwnBooks() throws Exception {
                // when(userService.getRentedBooks(1L)).thenReturn(rentedBooks);

                mockMvc.perform(get("/api/users/1/books").with(user(new CustomPersonDetails(users.get(0)))))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "user2", roles = { "STUDENT" })
        void studentShouldNotAccessOtherBooks() throws Exception {
                CustomPersonDetails studentWithId2 = new CustomPersonDetails(users.get(1));

                mockMvc.perform(get("/api/users/1/books").with(user(studentWithId2)))
                                .andExpect(status().isForbidden());
        }

        
        // UPDATE
        // PUT: updateStudent()
        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldUpdateStudentGivenValidInput() throws JsonProcessingException, Exception {
                Person student = new Person(1L, "user1", "1234", "updatedName", "Test", "Hans@email.com",
                                List.of(Role.STUDENT), "8b");

                when(userService.updatePerson(1l, student)).thenReturn(student);

                mockMvc.perform(put("/api/users/1").contentType("application/json")
                                .content(new ObjectMapper().writeValueAsString(student))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName").value("updatedName"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldThrowStudentNotFoundGivenInvalidId() throws JsonProcessingException, Exception {
                Person student = new Person(999L, "user999", "1234", "updatedName", "Test", "Hans@email.com",
                                List.of(Role.STUDENT), "8b");

                when(userService.updatePerson(999L, student)).thenThrow(PersonNotFoundException.class);

                mockMvc.perform(put("/api/users/999").contentType("application/json")
                                .content(new ObjectMapper().writeValueAsString(student)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturn400WhenUpdatingStudentWithInvalidData() throws Exception {
                // Create a Student object with invalid data (e.g., missing required fields)
                Person invalidUser = new Person();
                invalidUser.setId(1L); // Assuming ID is set for an update, but fields are empty
                invalidUser.setFirstName(""); // Invalid because @NotEmpty should trigger validation error
                invalidUser.setLastName(""); // Invalid because @NotEmpty should trigger validation error
                invalidUser.setClassName(""); // Invalid because @NotEmpty should trigger validation error
                invalidUser.setEmail("invalidEmail"); // Invalid because it doesn't match the email pattern

                // Perform a PUT request with invalid data
                mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUser)))
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
                Map<String, Object> updateFields = Map.of("firstName", "NewName", "email", "new.email@example.com");

                Person updatedUser = new Person(1L, "user1", "1234", "NewName", "Test1", "new.email@example.com",
                                List.of(Role.STUDENT), "8a");
                // When
                when(userService.updatePersonFields(eq(1L), any(Map.class))).thenReturn(updatedUser);
                // When/Then
                mockMvc.perform(patch("/api/users/1").contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(updateFields))
                // .with(httpBasic("sven", "1234"))
                ).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("NewName"))
                                .andExpect(jsonPath("$.email").value("new.email@example.com"));

                verify(userService, times(1)).updatePersonFields(eq(1L), any(Map.class));
        }

        @Test
        @WithMockUser(username = "user1", roles = { "STUDENT" })
        void shouldThrowUnauthorizedExceptionWhenUpdatingPerson() throws JsonProcessingException, Exception {
                // Given
                Person person = new Person(1L, "user1", "1234", "updatedName", "Test", "Hans@email.com",
                                List.of(Role.STUDENT), "8b");

                // when(personService.findById(1L)).then

                mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(person))).andExpect(status().isForbidden());

        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturn204GivenAdminUserPatchPasswordOfUser1ByOnlySendingNewPassword() throws JsonProcessingException, Exception{
                // Given
                ChangePasswordRequest req = new ChangePasswordRequest(null, "newPassword");
                // When
                doNothing().when(userService).patchPassword(1L, req);
                // Then

                mockMvc.perform(patch("/api/users/1/password").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))).andExpect(status().isNoContent());

                verify(userService, times(1)).patchPassword(1L, req);
        }

        

        
        // DELETE
        // DELETE: deleteStudentById()
        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldDeleteStudentWhenGivenValidId() throws Exception {
                doNothing().when(userService).deletePersonById(1L);

                mockMvc.perform(delete("/api/users/1")).andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenDeletingNonExistingStudent() throws Exception {
                doThrow(new PersonNotFoundException(999L)).when(userService).deletePersonById(999L);

                mockMvc.perform(delete("/api/users/999")).andExpect(status().isNotFound());
        }
        @Test
        @WithMockUser(roles = { "STUDENT" })
        void shouldThrowUnauthorizedExceptionWhenDeletingPersonAsStudent() throws Exception{
                doNothing().when(userService).deletePersonById(1L);
                mockMvc.perform(delete("/api/users/1")).andExpect(status().isForbidden());
        }

}
