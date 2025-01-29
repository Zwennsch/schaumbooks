package de.schaumburg.schaumbooks.user;

public class InvalidUserInputException extends RuntimeException{
    public InvalidUserInputException(String message){
        super("Invalid user input: "+ message);
    }
}



