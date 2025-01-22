package de.schaumburg.schaumbooks.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(long id, User user) {
        Optional<User> possibleUser = userRepository.findById(id);

        return possibleUser.map(existingUser -> {
            existingUser.setId(id);
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setClassName(user.getClassName());
            existingUser.setRoles(user.getRoles());
            existingUser.setEmail(user.getEmail());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new UserNotFoundException(id));

    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(user.getId());

    }

}
