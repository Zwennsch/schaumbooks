package de.schaumburg.schaumbooks.person;

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

import de.schaumburg.schaumbooks.book.Book;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(path = "/api/users")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    // CREATE
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ResponseEntity<Person> addPerson(@Valid @RequestBody Person person) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.save(person));
    }

    // READ
    @GetMapping("")
    public ResponseEntity<List<Person>> findAll() {
        List<Person> allUsers = personService.findAll();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Person> findPersonByUsername(@PathVariable String username) {
        return ResponseEntity.ok(personService.findPersonByUsername(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findPersonById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.findPersonById(id));
    }

    @PreAuthorize("hasRole('TEACHER') or #id == authentication.principal.id")
    @GetMapping("/{id}/books")
    public ResponseEntity<List<Book>> getRentedBooks(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getRentedBooks(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody @Valid Person person) {
        Person updatedPerson = personService.updatePerson(id, person);
        return ResponseEntity.ok(updatedPerson);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Person> updatePersonFields(@PathVariable Long id,
            @RequestBody Map<String, Object> fieldsToPatch) {
        Person updatedPerson = personService.updatePersonFields(id, fieldsToPatch);
        return ResponseEntity.ok(updatedPerson);
    }

    @PatchMapping("{id}/password")
    public ResponseEntity<?> patchPassword(@PathVariable Long id, @RequestBody ChangePasswordRequest reqPwds){
        personService.patchPassword(id, reqPwds);
        return ResponseEntity.noContent().build();
    }


    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonById(@PathVariable Long id) {
        personService.deletePersonById(id);
        return ResponseEntity.noContent().build();
    }

}


