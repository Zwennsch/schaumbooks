// package de.schaumburg.schaumbooks.user;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// // import org.springframework.http.MediaType;

// import de.schaumburg.schaumbooks.user.User;
// import de.schaumburg.schaumbooks.user.UserNotFoundException;
// import de.schaumburg.schaumbooks.user.UserRepository;
// import de.schaumburg.schaumbooks.user.UserService;

// // import com.fasterxml.jackson.core.JsonProcessingException;

// public class UserServiceTest {

//     @Mock
//     private UserRepository userRepository;

//     @InjectMocks
//     private UserService userService;

//     private List<User> students;

//     @BeforeEach
//     void setup() {
//         MockitoAnnotations.openMocks(this);
//         User student1 = new User(1L, "student1", "lastname1", "student1@mail.com", List.of(Role.STUDENT), "8a");
//         User student2 = new User(2L, "student2", "lastname2", "student2@mail.com", List.of(Role.STUDENT), "9a");

//         students = List.of(student1, student2);
//     }

//     // findAll()
//     @Test
//     void shouldReturnAllStudentsFromRepository() {
//         when(userRepository.findAll()).thenReturn(students);
//         List<User> allStudents = userService.findAll();

//         assertEquals("student2", allStudents.get(1).getFirstName());
//         verify(userRepository, times(1)).findAll();

//     }

//     // findById
//     @Test
//     void shouldFindStudentGivenValidId() {
//         when(userRepository.findById(1L)).thenReturn(Optional.of(students.get(0)));
//         User student = userService.findUserById(1L);

//         assertEquals(students.get(0), student);
//         verify(userRepository).findById(1L);
//     }

//     @Test
//     void shouldThrowExceptionWhenGivenInvalidId() {
//         when(userRepository.findById(99L)).thenReturn(Optional.empty());

//         assertThrows(UserNotFoundException.class, () -> userService.findUserById(99L));
//         verify(userRepository).findById(99L);
//     }

//     // save
//     @Test
//     void shouldAddNewStudentGivenValidInput() {
//         User student = new User(3L, "Hans", "Meier", "10a", "hans@mail.com");
//         when(userRepository.save(student)).thenReturn(student);

//         User savedStudent = userService.save(student);
//         assertEquals(student, savedStudent);
//         verify(userRepository).save(student);
//     }

//     // update
//     @Test
//     void shouldUpdateStudentGivenValidInput() {
//         User updatedStudent = new User(1L, "newName", "newLastName", "10a", "newMail@mail.com");
//         when(userRepository.findById(1L)).thenReturn(Optional.of(students.get(0)));
//         when(userRepository.save(updatedStudent)).thenReturn(updatedStudent);
//         User result = userService.updateUser(1L, updatedStudent);
//         assertEquals(updatedStudent, result);
//         verify(userRepository).save(updatedStudent);
//     }

//     @Test
//     void shouldThrowStudentNotFoundExceptionWhenUpdatingWithInvalidId() {
//         User updatedStudent = new User(99L, "newName", "newLastName", "10a", "newMail@mail.com");
//         when(userRepository.findById(99L)).thenReturn(Optional.empty());
//         assertThrows(UserNotFoundException.class, () -> userService.updateUser(99L, updatedStudent));
//     }

//     @Test
//     void shouldUpdateOnlySpecifiedFields() {
//         User existingStudent = new User(1L, "John", "Doe", "10a", "john.doe@mail.com");
//         User updatedStudent = new User(1L, "John", "Doe", "10a", "newMail@mail.com");

//         when(userRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
//         when(userRepository.save(existingStudent)).thenReturn(updatedStudent);

//         User result = userService.updateUser(1L, updatedStudent);

//         assertEquals(updatedStudent.getEmail(), result.getEmail());
//         assertEquals(existingStudent.getFirstName(), result.getFirstName());
//         assertEquals(existingStudent.getLastName(), result.getLastName());
//         verify(userRepository).save(existingStudent);
//     }

//     // Delete
//     @Test
//     void shouldDeleteStudentGivenValidId() {
//         when(userRepository.findById(1l)).thenReturn(Optional.of(students.get(0)));

//         userService.deleteUserById(1l);

//         verify(userRepository).deleteById(1l);
//     }

//     @Test
//     void shouldThrowStudentNotFoundExceptionGivenNoStudentWithIdFOund() {
//         when(userRepository.findById(99l)).thenReturn(Optional.empty());

//         assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(99l));

//         verify(userRepository, never()).deleteById(99l);
//     }

// }
