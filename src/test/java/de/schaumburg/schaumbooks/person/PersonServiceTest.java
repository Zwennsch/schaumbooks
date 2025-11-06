package de.schaumburg.schaumbooks.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// removed accidental preview API import
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
// import org.springframework.http.MediaType;
import org.mockito.internal.matchers.Null;
import org.springframework.security.test.context.support.WithMockUser;

import de.schaumburg.schaumbooks.book.Book;
import de.schaumburg.schaumbooks.book.BookRepository;
import de.schaumburg.schaumbooks.book.BookStatus;
import de.schaumburg.schaumbooks.person.Person;
import de.schaumburg.schaumbooks.person.PersonNotFoundException;
import de.schaumburg.schaumbooks.person.PersonRepository;
import de.schaumburg.schaumbooks.person.PersonService;

// import com.fasterxml.jackson.core.JsonProcessingException;

public class PersonServiceTest {

    @Mock
    private PersonRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private PersonService userService;

    private List<Person> users;
    private List<Book> rentedBooksForStudentId1;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        Person student1 = new Person(1L, "user1", "1234", "student1", "lastname1", "student1@mail.com",
                List.of(Role.STUDENT), "8a");
        Person student2 = new Person(2L, "user2", "1234", "student2", "lastname2", "student2@mail.com",
                List.of(Role.STUDENT), "9a");
        Person teacher = new Person(3L, "user3", "1234", "hans", "lehrer", "lehrer@mail.com",
                List.of(Role.TEACHER), null);

        users = List.of(student1, student2, teacher);

        // Create rented books for student1
        rentedBooksForStudentId1 = List.of(
                new Book(1l, "Book One", "Publisher One", "1234-5678", BookStatus.LENT, users.get(0)),
                new Book(2l, "Book Two", "Publisher Two", "1234-5679", BookStatus.LENT, users.get(0)));
    }

    // findAll()
    @Test
    void shouldReturnAllStudentsFromRepository() {
        when(userRepository.findAll()).thenReturn(users);
        List<Person> allStudents = userService.findAll();

        assertEquals("student2", allStudents.get(1).getFirstName());
        verify(userRepository, times(1)).findAll();

    }

    // findById
    @Test
    void shouldFindStudentGivenValidId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        Person student = userService.findPersonById(1L);

        assertEquals(users.get(0), student);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowPersonNotFoundExceptionWhenGivenInvalidId() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> userService.findPersonById(99L));
        verify(userRepository).findById(99L);
    }

    // findByUsername
    @Test
    void shouldFindUserGivenCorrectUsername() {
        // Given
        String username = "user1";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(users.get(0)));

        // When
        Person user = userService.findPersonByUsername("user1");
        // Then
        assertEquals(users.get(0), user);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void shouldThrowUserNotFoundExceptionGivenWrongUsername() {
        // Given
        String username = "wrongUsername";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PersonNotFoundException.class,
                () -> userService.findPersonByUsername(username));
        assertEquals("Person not found with username: wrongUsername", exception.getMessage());
        verify(userRepository).findByUsername(username);
    }

    // findBooksForId
    @Test
    void shouldReturnBooksForGivenUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(bookRepository.findByPerson(users.get(0))).thenReturn(rentedBooksForStudentId1);

        List<Book> books = bookRepository.findByPerson(users.get(0));
        assertEquals(2, books.size());
        verify(bookRepository).findByPerson(users.get(0));

    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGivenInvalidIdWhenGettingListOfBooks() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        // when
        Exception exception = assertThrows(PersonNotFoundException.class, () -> userService.getRentedBooks(99L));
        assertEquals("Person not found with id: 99", exception.getMessage());
        verify(userRepository).findById(99L);
    }

    // save
    @Test
    void shouldAddNewStudentGivenValidInput() {
        Person student = new Person(4L, "user4", "1234", "student3", "lastname3",
                "student3@mail.com",
                List.of(Role.STUDENT), "9a");
        when(userRepository.save(student)).thenReturn(student);

        Person savedStudent = userService.save(student);
        assertEquals(student, savedStudent);
        verify(userRepository).save(student);
    }

    @Test
    void shouldSaveAdminWithoutClassName() {
        Person admin = new Person(null, "admin2", "123456", "Jane", "Doe",
                "jane@example.com",
                List.of(Role.ADMIN), null);

        when(userRepository.save(admin)).thenReturn(admin);

        Person savedUser = userService.save(admin);

        assertNotNull(savedUser);
        assertNull(savedUser.getClassName());
        verify(userRepository, times(1)).save(admin);
    }

    @Test
    void shouldThrowInvalidPersonInputExceptionWhenGivenStudentWithoutClass() {
        Person studentNoClass = new Person(null, "hans", "12345", "hans", "meier",
                "hans@mail.com", List.of(Role.STUDENT),
                null);

        Exception exception = assertThrows(InvalidPersonInputException.class, () -> userService.save(studentNoClass));
        verify(userRepository, times(0)).save(studentNoClass);
        assertEquals("Invalid user input: Student must have a className",
                exception.getMessage());
    }

    @Test
    void shouldThrowInvalidPersonInputExceptionWhenGivenStudentWithEmptyClass() {
        Person studentNoClass = new Person(null, "hans", "12345", "hans", "meier",
                "hans@mail.com", List.of(Role.STUDENT),
                "");

        Exception exception = assertThrows(InvalidPersonInputException.class, () -> userService.save(studentNoClass));
        verify(userRepository, times(0)).save(studentNoClass);
        assertEquals("Invalid user input: Student must have a className",
                exception.getMessage());
    }

    @Test
    void shouldThrowInvalidPersonInputExceptionWhenGivenTeacherWithClassName() {
        Person teacherWithClass = new Person(null, "hans", "12345", "hans", "meier",
                "hans@mail.com", List.of(Role.TEACHER),
                "10a");

        Exception exception = assertThrows(InvalidPersonInputException.class, () -> userService.save(teacherWithClass));
        verify(userRepository, times(0)).save(teacherWithClass);
        assertEquals("Invalid user input: Non-Student roles must not have a className", exception.getMessage());
    }

    @Test
    void shouldAddDefaultStudentRoleWhenAddingNewUserWithoutRole() {
        // Given
        Person user = new Person(null, "hans", "12345", "hans", "meier",
                "hans@mail.com", null, "10a");
        Person returnedUser = new Person(1L, "hans", "12345", "hans", "meier",
                "hans@mail.com", List.of(Role.STUDENT),
                "10a");
        // When
        when(userRepository.save(any(Person.class))).thenReturn(returnedUser);
        Person savedUser = userService.save(user);
        // Then
        assertNotNull(savedUser);
        assertEquals(List.of(Role.STUDENT), savedUser.getRoles());
        verify(userRepository).save(any(Person.class));
    }

    @Test
    void shouldAddDefaultStudentRoleWhenAddingNewUserWithoutEmptyStringAsRole() {
        // Given
        Person user = new Person(null, "hans", "12345", "hans", "meier", "hans@mail.com",
                List.of(), "10a");
        Person returnedUser = new Person(1L, "hans", "12345", "hans", "meier",
                "hans@mail.com", List.of(Role.STUDENT),
                "10a");
        // When
        when(userRepository.save(any(Person.class))).thenReturn(returnedUser);
        Person savedUser = userService.save(user);
        // Then
        assertNotNull(savedUser);
        assertEquals(List.of(Role.STUDENT), savedUser.getRoles());
        verify(userRepository).save(any(Person.class));
    }

    // @Test
    // void shouldNotOverrideExistingRoles() {
    // // Given
    // Person user = new Person(null, "hans", "12345", "hans", "meier",
    // "hans@mail.com", List.of(Role.ADMIN), null);
    // // When
    // when(userRepository.save(any(Person.class))).thenReturn(user);
    // Person savedUser = userService.save(user);
    // // Then
    // assertNotNull(savedUser);
    // assertEquals(List.of(Role.ADMIN), savedUser.getRoles());
    // }

    @Test
    void shouldThrowInvalidUserInputExceptionWhenUsernameIsAlreadyExisting() {
        // Given
        Person user = new Person(null, "user1", "12345", "hans", "Müller",
                "email@hans.com", List.of(Role.STUDENT), "10a");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        // When/Then
        Exception exception = assertThrows(InvalidPersonInputException.class, () -> userService.save(user));
        verify(userRepository).findByUsername(user.getUsername());
        verify(userRepository, times(0)).save(any(Person.class));
        assertEquals("Invalid user input: Username already taken: user1",
                exception.getMessage());
    }

    // update
    @Test
    void shouldUpdateStudentGivenValidInput() {
        Person updatedStudent = new Person(1L, "user1", "1234", "newName",
                "newLastName", "newMail@mail.com",
                List.of(Role.STUDENT), "10b");
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(userRepository.save(updatedStudent)).thenReturn(updatedStudent);
        Person result = userService.updatePerson(1L, updatedStudent);
        assertEquals(updatedStudent, result);
        verify(userRepository).save(updatedStudent);
    }

    @Test
    void shouldThrowStudentNotFoundExceptionWhenUpdatingWithInvalidId() {
        Person updatedStudent = new Person(99L, "user99", "1234", "newName",
                "newLastName", "newMail@mail.com",
                List.of(Role.STUDENT), "10a");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PersonNotFoundException.class, () -> userService.updatePerson(99L, updatedStudent));
    }

    @Test
    void shouldUpdateOnlySpecifiedFields() {
        Person existingStudent = new Person(1L, "user1", "1234", "John", "Doe",
                "john.doe@mail.com", List.of(Role.STUDENT),
                "10a");
        Person updatedEmailStudent = new Person(1L, "user1", "1234", "John", "Doe",
                "newMail@mail.com",
                List.of(Role.STUDENT), "10a");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(userRepository.save(existingStudent)).thenReturn(updatedEmailStudent);

        Person result = userService.updatePerson(1L, updatedEmailStudent);

        assertEquals(updatedEmailStudent.getEmail(), result.getEmail());
        assertEquals(existingStudent.getFirstName(), result.getFirstName());
        assertEquals(existingStudent.getLastName(), result.getLastName());
        verify(userRepository).save(existingStudent);
    }

    @Test
    void shouldUpdateFirstNameAndEmail() {
        // Arrange
        Person user = new Person(5L, "john1", "1234", "John", "Doe",
                "john@example.com", List.of(Role.STUDENT),
                "Old Class");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("firstName", "New Name");
        updateFields.put("email", "new.email@example.com");

        // Act
        Person updatedUser = userService.updatePersonFields(5L, updateFields);

        // Assert
        assertEquals("New Name", updatedUser.getFirstName());
        assertEquals("new.email@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionForInvalidField() {
        // Arrange
        Person user = new Person(5L, "john1", "1234", "John", "Doe",
                "john@example.com", List.of(Role.STUDENT),
                "Old Class");
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("invalidField", "value");

        // Act & Assert
        assertThrows(InvalidPersonInputException.class, () -> userService.updatePersonFields(5L, updateFields));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenPatchingWithWrongId() {
        // Given
        Long id = 99999L;
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("username", "value");

        // When/Then
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(PersonNotFoundException.class, () -> userService.updatePersonFields(id, updateFields));
    }

    @Test
    void shouldNotUpdateUserWhenUsernameAlreadyTaken() {
        // Given
        String takenUsername = users.get(0).getUsername();
        Map<String, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("username", takenUsername);
        Person userBefore = new Person(5L, "validUsername", "1234", "John", "Doe",
                "john@example.com",
                List.of(Role.STUDENT), "Old Class");
        Person userToUpdate = new Person(5L, takenUsername, "1234", "John", "Doe",
                "john@example.com",
                List.of(Role.STUDENT), "Old Class");
        when(userRepository.findById(5L)).thenReturn(Optional.of(userBefore));
        when(userRepository.findByUsername(takenUsername)).thenReturn(Optional.of(users.get(0)));

        // When/Then
        assertThrows(InvalidPersonInputException.class, () -> userService.updatePerson(5L, userToUpdate));
        assertThrows(InvalidPersonInputException.class, () -> userService.updatePersonFields(5L, fieldsToUpdate));
        verify(userRepository, never()).save(any(Person.class));
    }

    @Test
    void shouldUpdateEmailSuccessfully() {
        // Arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "newemail@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        // TODO: This seems wrong: userRepository.save should be called with the
        // modified user
        when(userRepository.save(any(Person.class))).thenReturn(users.get(0));

        // Act
        Person updatedUser = userService.updatePersonFields(1L, updates);

        // Assert
        assertEquals("newemail@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).save(users.get(0));
    }

    // Delete
    @Test
    void shouldDeleteStudentGivenValidId() {
        when(userRepository.findById(1l)).thenReturn(Optional.of(users.get(0)));

        userService.deletePersonById(1l);

        verify(userRepository).deleteById(1l);
    }

    @Test
    void shouldThrowStudentNotFoundExceptionGivenNoStudentWithIdFOund() {
        when(userRepository.findById(99l)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> userService.deletePersonById(99l));

        verify(userRepository, never()).deleteById(99l);
    }
}
