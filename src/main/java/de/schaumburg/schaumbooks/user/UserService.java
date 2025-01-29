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
        if (user.getRoles().contains(Role.STUDENT) && (user.getClassName() == null || user.getClassName().isEmpty())){
            throw new InvalidUserInputException("Student must have a className");
        }else if(!user.getRoles().contains(Role.STUDENT) && user.getClassName() != null){
            throw new InvalidUserInputException("Non-Student roles must not have a className");
        }
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
