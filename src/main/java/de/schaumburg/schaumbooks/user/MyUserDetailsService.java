// package de.schaumburg.schaumbooks.user;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// @Service
// public class MyUserDetailsService implements UserDetailsService {

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//         Optional<User> user = userRepository.findByUsername(username);
//         if (user.isPresent()) {
//             var userObject = user.get();
//             return org.springframework.security.core.userdetails.User.builder()
//                     .roles(getRoles(userObject.getRoles()))
//                     .username(userObject.getUsername())
//                     .password(userObject.getPassword()).build();
//         } else {
//             throw new UserNotFoundException(username);
//         }
//     }

//     private String[] getRoles(List<Role> roles) {

//         return roles.stream()
//                 .map(Role::name)
//                 .toArray(String[]::new);
//     }

// }
