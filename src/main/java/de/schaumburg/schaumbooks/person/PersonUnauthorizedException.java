package de.schaumburg.schaumbooks.person;

public class PersonUnauthorizedException extends RuntimeException {
    public PersonUnauthorizedException(Long id) {
        super("Person with id " + id + " is not authorized to perfom action");
    }
}

// package de.schaumburg.schaumbooks.person;

// public class PersonNotFoundException extends RuntimeException {
//     public PersonNotFoundException(Long id) {
//         // keep the original message text to remain compatible with existing tests
//         super("Person not found with id: " + id);
//     }

//     public PersonNotFoundException(String name) {
//         super("Person not found with username: " + name);
//     }

// }

