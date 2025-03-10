package de.schaumburg.schaumbooks.user;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import de.schaumburg.schaumbooks.book.Book;
import de.schaumburg.schaumbooks.book.BookRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserService(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Transactional
    public User save(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of(Role.STUDENT));
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new InvalidUserInputException("Username already taken: " + user.getUsername());
        }
        if (user.getRoles().contains(Role.STUDENT) && (user.getClassName() == null || user.getClassName().isEmpty())) {
            throw new InvalidUserInputException("Student must have a className");
        } else if (!user.getRoles().contains(Role.STUDENT) && (user.getClassName() != null)) {
            throw new InvalidUserInputException("Non-Student roles must not have a className");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(long id, User user) {
        Optional<User> possibleUser = userRepository.findById(id);
        if (usernameExistsInDB(user.getUsername())){
            throw new InvalidUserInputException("User with username: "+ user.getUsername() + " already exits");
        }

        return possibleUser.map(existingUser -> {
            existingUser.setId(id);
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setClassName(user.getClassName());
            existingUser.setRoles(user.getRoles());
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setUsername(user.getUsername());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public User updateUserFields(Long userId, Map<String, Object> fieldsToPatch) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        fieldsToPatch.forEach((key, value) -> {
            

            Field field = ReflectionUtils.findField(User.class, key);
            if (field == null) {
                throw new InvalidUserInputException("No field named " + key);
            }
            if(key == "username" && usernameExistsInDB((String)value)){
                throw new InvalidUserInputException("Username already taken");
            }
            field.setAccessible(true);
            ReflectionUtils.setField(field, user, value);
        });

        return userRepository.save(user);

    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(user.getId());

    }

    boolean usernameExistsInDB(String username){
        return userRepository.findByUsername(username).isPresent();
    }

    public List<Book> getRentedBooks(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        return bookRepository.findByUser(user);
    }

}
