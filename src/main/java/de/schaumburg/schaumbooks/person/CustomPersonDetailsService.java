package de.schaumburg.schaumbooks.person;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.validation.constraints.NotNull;

@Service
public class CustomPersonDetailsService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
        Objects.requireNonNull(username);

        Optional<Person> person = personRepository.findByUsername(username);
        if (person.isPresent()) {
            var personObject = person.get();
            return new CustomPersonDetails(personObject);
        } else {
            throw new PersonNotFoundException(username);
        }
    }

}
