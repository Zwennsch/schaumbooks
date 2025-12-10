package de.schaumburg.schaumbooks.person;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import de.schaumburg.schaumbooks.book.Book;
import de.schaumburg.schaumbooks.book.BookRepository;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final BookRepository bookRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    public PersonService(PersonRepository personRepository, BookRepository bookRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    @Transactional
    public Person save(Person person) {
        if (person.getRoles() == null || person.getRoles().isEmpty()) {
            person.setRoles(List.of(Role.STUDENT));
        }

        if (personRepository.findByUsername(person.getUsername()).isPresent()) {
            throw new InvalidPersonInputException("Username already taken: " + person.getUsername());
        }
        if (person.getRoles().contains(Role.STUDENT)
                && (person.getClassName() == null || person.getClassName().isEmpty())) {
            throw new InvalidPersonInputException("Student must have a className");
        } else if (!person.getRoles().contains(Role.STUDENT) && (person.getClassName() != null)) {
            throw new InvalidPersonInputException("Non-Student roles must not have a className");
        }
        if (person.getPassword() == null || person.getPassword().isEmpty()) {
            throw new InvalidPersonInputException("Password must not be empty");
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));

        return personRepository.save(person);
    }

    // READ
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findPersonById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    public Person findPersonByUsername(String username) {
        return personRepository.findByUsername(username)
                .orElseThrow(() -> new PersonNotFoundException(username));
    }

    boolean usernameExistsInDB(String username) {
        return personRepository.findByUsername(username).isPresent();
    }

    public List<Book> getRentedBooks(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));

        return bookRepository.findByPerson(person);
    }

    public boolean hasRole(Long personId, Role role) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        return person.getRoles().contains(role);
    }

    // UPDATE
    @Transactional
    public Person updatePerson(long id, Person person) {
        Optional<Person> possibleUser = personRepository.findById(id);
        if (usernameExistsInDB(person.getUsername())) {
            throw new InvalidPersonInputException("Person with username: " + person.getUsername() + " already exits");
        }

        return possibleUser.map(existingUser -> {
            existingUser.setId(id);
            existingUser.setFirstName(person.getFirstName());
            existingUser.setLastName(person.getLastName());
            existingUser.setClassName(person.getClassName());
            existingUser.setRoles(person.getRoles());
            existingUser.setEmail(person.getEmail());
            if (person.getPassword() != null && !person.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(person.getPassword()));
            }
            existingUser.setUsername(person.getUsername());
            return personRepository.save(existingUser);
        }).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @Transactional
    public Person updatePersonFields(Long personId, Map<String, Object> fieldsToPatch) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));

        for (Map.Entry<String, Object> e : fieldsToPatch.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();

            Field field = ReflectionUtils.findField(Person.class, key);
            if (field == null) {
                throw new InvalidPersonInputException("No field named " + key);
            }

            if ("username".equals(key) && usernameExistsInDB((String) value)) {
                throw new InvalidPersonInputException("Username already taken");
            }

            if ("password".equals(key)) {
                person.setPassword(passwordEncoder.encode(value.toString()));
                continue;
            }

            field.setAccessible(true);
            ReflectionUtils.setField(field, person, value);
        }

        return personRepository.save(person);

    }
// TODO: add security
    public void patchPassword(Long personId, ChangePasswordRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // logger.info("logged in authorities:"+auth.getAuthorities().toString());
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        // Check if user has no ADMIN role
        if (auth != null && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            //  - if so, do  old password check
            logger.info("logged in as non-admin user");
            if (!passwordEncoder.matches(req.oldPassword(), person.getPassword())) {
                throw new InvalidPersonInputException("Old password is incorrect");
            }
        } 
        person.setPassword(passwordEncoder.encode(req.newPassword()));
        personRepository.save(person);
    }

    // DELETE
    @Transactional
    public void deletePersonById(Long id) {
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        personRepository.deleteById(person.getId());

    }

}
