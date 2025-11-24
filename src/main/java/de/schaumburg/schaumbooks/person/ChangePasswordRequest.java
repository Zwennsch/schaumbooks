package de.schaumburg.schaumbooks.person;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
    
}
