package de.schaumburg.schaumbooks.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import de.schaumburg.schaumbooks.user.CustomUserDetailsService;

// import de.schaumburg.schaumbooks.user.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class TestSecurityConfig {

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // first you declare WHAT you want to protect and how:
                .authorizeHttpRequests(
                        authorizeHttp -> {
                            // TODO: remove later in production
                            authorizeHttp.requestMatchers("/h2-console/**").permitAll();
                            // ensures that every request requires authentication
                            authorizeHttp.anyRequest().authenticated();
                        })
                // secondly you declare HOW you want to login:
                // disable csrf since making the API stateless
                .csrf(csrfConfigCustomizer -> csrfConfigCustomizer.disable())
                // TODO: This is just for testing with h2-database. Has to be removed in production
                .headers(headers -> headers
                        .addHeaderWriter(
                                new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }

    // This is just for testing because in a real application those details should
    // come from the database:
//     @Bean
//     public UserDetailsService userDetailsService() {
//         UserDetails user1 = User.withDefaultPasswordEncoder()
//                 .username("sven")
//                 .password("1234")
//                 // .roles("USER")
//                 .build();
//         UserDetails user2 = User.withDefaultPasswordEncoder()
//                 .username("hans")
//                 .password("3456")
//                 // .roles("USER")
//                 .build();
//         return new InMemoryUserDetailsManager(user1, user2);
//     }

// TODO: Set proper PasswordEncoder!
    @Bean
    public AuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
    provider.setUserDetailsService(userDetailsService);
    return provider;
    }
}
