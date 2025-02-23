package de.schaumburg.schaumbooks.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.foreign.Linker.Option;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
// import org.springframework.http.MediaType;
import org.mockito.internal.matchers.Null;

import de.schaumburg.schaumbooks.user.User;
import de.schaumburg.schaumbooks.user.UserNotFoundException;
import de.schaumburg.schaumbooks.user.UserRepository;
import de.schaumburg.schaumbooks.user.UserService;

// import com.fasterxml.jackson.core.JsonProcessingException;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private List<User> users;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        User student1 = new User(1L,"user1", "1234", "student1", "lastname1", "student1@mail.com", List.of(Role.STUDENT), "8a");
        User student2 = new User(2L, "user2", "1234", "student2", "lastname2", "student2@mail.com", List.of(Role.STUDENT), "9a");
        User teacher =  new User(3L, "user3", "1234", "hans", "lehrer", "lehrer@mail.com", List.of(Role.TEACHER), null);
        
        users = List.of(student1, student2, teacher);
    }
    
    // findAll()
    @Test
    void shouldReturnAllStudentsFromRepository() {
        when(userRepository.findAll()).thenReturn(users);
        List<User> allStudents = userService.findAll();
        
        assertEquals("student2", allStudents.get(1).getFirstName());
        verify(userRepository, times(1)).findAll();

    }
    
    // findById
    @Test
    void shouldFindStudentGivenValidId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        User student = userService.findUserById(1L);

        assertEquals(users.get(0), student);
        verify(userRepository).findById(1L);
    }
    
    @Test
    void shouldThrowUserNotFoundExceptionWhenGivenInvalidId() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(99L));
        verify(userRepository).findById(99L);
    }

    // findByUsername
    @Test
    void shouldFindUserGivenCorrectUsername(){
        // Given
        String username = "user1";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(users.get(0)));
        
        // When
        User user = userService.findUserByUsername("user1");
        // Then
        assertEquals(users.get(0), user);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test 
    void shouldThrowUserNotFoundExceptionGivenWrongUsername(){
        // Given
        String username = "wrongUsername";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }
    
    // save
    @Test
    void shouldAddNewStudentGivenValidInput() {
        User student = new User(4L, "user4", "1234", "student3", "lastname3", "student3@mail.com", List.of(Role.STUDENT), "9a");
        when(userRepository.save(student)).thenReturn(student);

        User savedStudent = userService.save(student);
        assertEquals(student, savedStudent);
        verify(userRepository).save(student);
    }

    @Test
    void shouldSaveAdminWithoutClassName() {
        User admin = new User(null, "admin2", "123456", "Jane", "Doe", "jane@example.com",
                List.of(Role.ADMIN), null);

        when(userRepository.save(admin)).thenReturn(admin);

        User savedUser = userService.save(admin);

        assertNotNull(savedUser);
        assertNull(savedUser.getClassName());
        verify(userRepository, times(1)).save(admin);
    }

    @Test
    void shouldThrowInvalidUserInputExceptionWhenGivenStudentWithoutClass(){
        User studentNoClass = new User(null, "hans", "12345", "hans", "meier", "hans@mail.com", List.of(Role.STUDENT), null);

        Exception exception = assertThrows(InvalidUserInputException.class, () -> userService.save(studentNoClass));
        verify(userRepository, times(0)).save(studentNoClass);
        assertEquals("Invalid user input: Student must have a className", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidUserInputExceptionWhenGivenTeacherWithClassName(){
        User teacherClass = new User(null, "hans", "12345", "hans", "meier", "hans@mail.com", List.of(Role.TEACHER), "10a");

        Exception exception = assertThrows(InvalidUserInputException.class, () -> userService.save(teacherClass));
        verify(userRepository, times(0)).save(teacherClass);
        assertEquals("Invalid user input: Non-Student roles must not have a className", exception.getMessage());
    }

    // update
    @Test
    void shouldUpdateStudentGivenValidInput() {
        User updatedStudent = new User(1L,"user1", "1234", "newName", "newLastName", "newMail@mail.com", List.of(Role.STUDENT), "10b" );
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(userRepository.save(updatedStudent)).thenReturn(updatedStudent);
        User result = userService.updateUser(1L, updatedStudent);
        assertEquals(updatedStudent, result);
        verify(userRepository).save(updatedStudent);
    }

    @Test
    void shouldThrowStudentNotFoundExceptionWhenUpdatingWithInvalidId() {
        User updatedStudent = new User(99L,"user99", "1234", "newName", "newLastName", "newMail@mail.com", List.of(Role.STUDENT), "10a");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(99L, updatedStudent));
    }

    @Test
    void shouldUpdateOnlySpecifiedFields() {
        User existingStudent = new User(1L, "user1", "1234", "John", "Doe", "john.doe@mail.com", List.of(Role.STUDENT), "10a");
        User updatedStudent =  new User(1L, "user1", "1234", "John", "Doe", "newMail@mail.com", List.of(Role.STUDENT), "10a");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(userRepository.save(existingStudent)).thenReturn(updatedStudent);

        User result = userService.updateUser(1L, updatedStudent);

        assertEquals(updatedStudent.getEmail(), result.getEmail());
        assertEquals(existingStudent.getFirstName(), result.getFirstName());
        assertEquals(existingStudent.getLastName(), result.getLastName());
        verify(userRepository).save(existingStudent);
    }

    // Delete
    @Test
    void shouldDeleteStudentGivenValidId() {
        when(userRepository.findById(1l)).thenReturn(Optional.of(users.get(0)));

        userService.deleteUserById(1l);

        verify(userRepository).deleteById(1l);
    }

    @Test
    void shouldThrowStudentNotFoundExceptionGivenNoStudentWithIdFOund() {
        when(userRepository.findById(99l)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(99l));

        verify(userRepository, never()).deleteById(99l);
    }

}
