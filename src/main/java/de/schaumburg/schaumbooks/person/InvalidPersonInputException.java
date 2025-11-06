package de.schaumburg.schaumbooks.person;

public class InvalidPersonInputException extends RuntimeException {
    public InvalidPersonInputException(String message) {
        // preserve original message wording
        super("Invalid user input: " + message);
    }
}
