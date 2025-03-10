package de.schaumburg.schaumbooks.user;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.schaumburg.schaumbooks.book.Book;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService studentService) {
        this.userService = studentService;
    }

    @GetMapping("")
    public ResponseEntity<List<User>> findAll() {
        List<User> allUsers = userService.findAll();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    // Get rented books for specific user
    // Only for student with id {id}, teacher and admin
    @GetMapping("/{id}/books")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or #id == authentication.principal.id")
    public ResponseEntity<List<Book>> getRentedBooks(@PathVariable Long id) {
       return ResponseEntity.ok(userService.getRentedBooks(id));
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserFields(@PathVariable Long id,
            @RequestBody Map<String, Object> fieldsToPatch) {
        User updatedUser = userService.updateUserFields(id, fieldsToPatch);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }


}
