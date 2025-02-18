package de.schaumburg.schaumbooks.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String name){
        super("User not found with username: "+ name);
    }

}
